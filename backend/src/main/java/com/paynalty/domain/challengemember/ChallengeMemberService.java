package com.paynalty.domain.challengemember;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserRepository;
import com.paynalty.domain.user.UserService;
import com.paynalty.global.error.ChallengeMemberErrorCode;
import com.paynalty.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeMemberService {
    private final ChallengeMemberRepository challengeMemberRepository;
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    // 챌린지 참여 중 인 맴버 목록 불러오기
    public List<ChallengeMemberResponse> getMembersByChallengeId(Long challengeId) {
        List<ChallengeMember> members = challengeMemberRepository.findByChallengeId(challengeId);
        return members.stream()
                .map(ChallengeMemberResponse::from)
                .collect(Collectors.toList());
    }

    // 챌린지 맴버 이미 있는지 검증 하고 추가
    @Transactional
    public void addMemberIfNotExists(User user, Challenge challenge) {
        // 이미 챌린지 멤버인지 확인
        boolean isAlreadyMember = challengeMemberRepository
                .findByUserTossIdAndChallengeId(user.getTossId(), challenge.getId())
                .isPresent();

        MemberRole role = MemberRole.CHALLENGER;
        if (!isAlreadyMember) {
            // 챌린지의 user 과 매개변수 user 이 서로 같다면 role = creator 아니면 challenger
            if (Objects.equals(challenge.getUser().getTossId(), user.getTossId())) {
                role = MemberRole.CREATOR;
            }

            ChallengeMember challengeMember = ChallengeMember.builder()
                    .user(user)
                    .challenge(challenge)
                    .isSuccess(challenge.getStatus()) // 초기 상태 설정
                    .endAt(challenge.getEndDate())
                    .role(role)
                    .build();
            challengeMemberRepository.save(challengeMember);
        }
    }

    // 챌린지 생성 시 생성자와 초대 친구들 챌린지 맴버로 생성
    @Transactional
    public void addMembersToNewChallenge(User creator, Challenge challenge, List<Long> tossIds) {
        // 1. 생성자 본인 추가
        addMemberIfNotExists(creator, challenge);

        if (tossIds == null || tossIds.isEmpty()) {
            return;
        }

        // tossIds 값으로 user찾아서 list에 넣고 아래 초대된 친구 추가에 전달
        List<User> users = userRepository.findAllByTossIdIn(tossIds);

        // 2. 초대된 친구들 추가
        for (User user : users) {
            addMemberIfNotExists(user, challenge);
        }
    }

    // 챌린지 생성자가 챌린지 멤버 수정
    @Transactional
    public void updateChallengeMembers(Long challengeId, List<Long> updateMemberTossIds, Long requesterUserId) {

        // 챌린지와 정보 조회 및 권한 확인
        ChallengeMember requesterMember = challengeMemberRepository
                .findByChallengeIdAndUserIdWithFetch(challengeId, requesterUserId)
                .orElseThrow(() -> new CustomException(ChallengeMemberErrorCode.NOT_CHALLENGE_MEMBER));

        if (requesterMember.getRole() != MemberRole.CREATOR) {
            throw new CustomException(ChallengeMemberErrorCode.NOT_CREATOR);
        }

        Challenge challenge = requesterMember.getChallenge();

        // 수정 목록에 생성자 포함 여부 확인
        Long requesterTossId = requesterMember.getUser().getTossId();
        if (!updateMemberTossIds.contains(requesterTossId)) {
            throw new CustomException(ChallengeMemberErrorCode.CREATOR_CANNOT_BE_REMOVED);
        }

        // DB에 저장된 멤버 목록 조회
        List<ChallengeMember> currentMembers = challengeMemberRepository.findByChallengeId(challengeId);

        Set<Long> currentMemberTossIds = currentMembers.stream()
                .map(member -> member.getUser().getTossId())
                .collect(Collectors.toSet());

        Set<Long> updateMemberTossIdsSet = new HashSet<>(updateMemberTossIds);

        // 삭제할 멤버 찾기
        List<ChallengeMember> membersToRemove = currentMembers.stream()
                .filter(member -> !updateMemberTossIdsSet.contains(member.getUser().getTossId()))
                .collect(Collectors.toList());

        // 추가할 멤버 찾기
        Set<Long> memberIdsToAdd = updateMemberTossIdsSet.stream()
                .filter(member -> !currentMemberTossIds.contains(member))
                .collect(Collectors.toSet());

        // 멤버 업데이트
        if (!membersToRemove.isEmpty()) {
            challengeMemberRepository.deleteAll(membersToRemove);
        }

        if (!memberIdsToAdd.isEmpty()) {
            List<User> userToAdd = userRepository.findAllByTossIdIn(new ArrayList<>(memberIdsToAdd));
            List<ChallengeMember> updateChallengeMembers = userToAdd.stream()
                    .map(user -> ChallengeMember.builder()
                            .user(user)
                            .challenge(challenge)
                            .role(MemberRole.CHALLENGER)
                            .isSuccess(challenge.getStatus())
                            .endAt(challenge.getEndDate())
                            .build())
                    .collect(Collectors.toList());
            challengeMemberRepository.saveAll(updateChallengeMembers);
        }
    }

    // 챌린지 탈퇴
    @Transactional
    public void withdrawChallenge(Long challengeId, Long userId) {
        ChallengeMember member = challengeMemberRepository
                .findByChallengeIdAndUserIdWithFetch(challengeId, userId)
                .orElseThrow(() -> new CustomException(ChallengeMemberErrorCode.NOT_CHALLENGE_MEMBER));

        if (member.getRole() == MemberRole.CREATOR) {
            throw new CustomException(ChallengeMemberErrorCode.CREATOR_CANNOT_BE_REMOVED);
        }

        challengeMemberRepository.delete(member);
    }

    /**
     * 초대 친구 정보를 담는 DTO
     */
    public static class InviteFriendInfo {
        private final String name;
        private final String phoneNumber;

        public InviteFriendInfo(String name, String phoneNumber) {
            this.name = name;
            this.phoneNumber = phoneNumber;
        }

        public String getName() {
            return name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }
    }

}
