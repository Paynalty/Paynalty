package com.paynalty.domain.challengemember;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserRepository;
import com.paynalty.domain.user.UserService;
import com.paynalty.global.error.ChallengeErrorCode;
import com.paynalty.global.error.CustomException;
import com.paynalty.global.error.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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


    // 챌린지 맴버 추가
    @Transactional
    public void addMemberIfNotExists(User user, Challenge challenge) {
        // 이미 챌린지 멤버인지 확인
        boolean isAlreadyMember = challengeMemberRepository
                .findByUserIdAndChallengeId(user.getId(), challenge.getId())
                .isPresent();
        
        if (!isAlreadyMember) {
            ChallengeMember challengeMember = ChallengeMember.builder()
                    .user(user)
                    .challenge(challenge)
                    .isSuccess(challenge.getStatus()) // 초기 상태 설정
                    .endAt(challenge.getEndDate())
                    .build();
            challengeMemberRepository.save(challengeMember);
        }
    }


    // 챌린지 생성 시 생성자와 초대 친구들 챌린지 맴버로 생성
    @Transactional
    public void addMembersToNewChallenge(User creator, Challenge challenge, List<InviteFriendInfo> inviteFriends) {
        // 1. 생성자 본인 추가
        addMemberIfNotExists(creator, challenge);

        // 2. 초대된 친구들 추가
        if (inviteFriends != null && !inviteFriends.isEmpty()) {
            for (InviteFriendInfo inviteFriend : inviteFriends) {
                // 이름과 전화번호로 사용자 찾기
                userRepository.findByNameAndPhoneNum(inviteFriend.getName(), inviteFriend.getPhoneNumber())
                        .ifPresent(friend -> addMemberIfNotExists(friend, challenge));
            }
        }
    }


    // 챌린지 생성 이후 친구 챌린지 맴버로 초대
    // 1.챌린지 유효성 검사
    // 2.초대한 친구가 user데이터 존재 하는지 검사
    // 3.이미 챌린지 맴버인지 검사
    // 4.챌린지 맴버 데이터 생성
    @Transactional
    public String addMemberByInvitation(Long challengeId, String inviteName, String invitePhoneNum) {
        
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ChallengeErrorCode.CHALLENGE_NOT_FOUND));
        
        User invite = userRepository.findByNameAndPhoneNum(inviteName, invitePhoneNum)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        
        boolean isAlreadyMember = challengeMemberRepository
                .findByUserIdAndChallengeId(invite.getId(), challengeId)
                .isPresent();
        
        if (isAlreadyMember) {
            return "이미 챌린지 멤버입니다.";
        }

        addMemberIfNotExists(invite, challenge);
        
        return "챌린지 맴버로 초대 되었습니다.";
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
