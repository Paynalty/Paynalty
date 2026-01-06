package com.paynalty.domain.challengemember;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserRepository;
import com.paynalty.domain.user.UserService;
import com.paynalty.global.error.ChallengeMemberErrorCode;
import com.paynalty.global.error.ChallengeErrorCode;
import com.paynalty.global.error.CustomException;
import com.paynalty.global.error.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
            if(Objects.equals(challenge.getUser().getTossId(), user.getTossId())){
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
    public void addMembersToNewChallenge(User creator, Challenge challenge, List<Long> userIds) {
        // 1. 생성자 본인 추가
        addMemberIfNotExists(creator, challenge);

        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        // usersId 값으로 user찾아서 list에 넣고 아래 초대된 친구 추가에 전달
        List<User> users = userRepository.findAllByTossIdIn(userIds);


        // 2. 초대된 친구들 추가
        for (User user : users) {
            addMemberIfNotExists(user, challenge);
        }
    }


    // 안쓰고있는것
    @Transactional
    public String addMemberByInvitation(Long challengeId, Long inviterTossId, String inviteName, String invitePhoneNum) {
        
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ChallengeErrorCode.CHALLENGE_NOT_FOUND));

        // 0. 초대하는 사람이 챌린지 멤버인지 확인
        boolean isInviterMember = challengeMemberRepository
                .findByUserTossIdAndChallengeId(inviterTossId, challengeId)
                .isPresent();
        
        if (!isInviterMember) {
            throw new CustomException(ChallengeMemberErrorCode.NOT_CHALLENGE_MEMBER);
        }
        
        User invite = userRepository.findByNameAndPhoneNum(inviteName, invitePhoneNum)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        
        boolean isAlreadyMember = challengeMemberRepository
                .findByUserTossIdAndChallengeId(invite.getTossId(), challengeId)
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
