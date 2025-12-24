package com.paynalty.domain.challengeverification;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeVerificationService {
    private final ChallengeVerificationRepository challengeVerificationRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    @Transactional
    public ChallengeVerificationResponse create(Long challengeId, String authEmail, ChallengeVerificationRequest request) {
        // 챌린지 존재 확인
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 사용자 존재 확인
        User user = userRepository.findByEmailOrName(authEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // ChallengeVerification 생성
        ChallengeVerification challengeVerification = ChallengeVerification.builder()
                .user(user)
                .challenge(challenge)
                .imageUrl(request.getImageUrl())
                .status(VerificationStatus.SUCCESS)
                .date(LocalDate.now())
                .build();

        ChallengeVerification saved = challengeVerificationRepository.save(challengeVerification);

        return ChallengeVerificationResponse.from(saved);
    }

    public Boolean checkVerification(Long challengeId, Long userId){
        return challengeRepository.findByChallengeIdAndUserId(challengeId, userId);
    }

    public ChallengeVerificationResponse findMyLatestVerification(Long challengeId, Long userId) {
        ChallengeVerification cv = challengeVerificationRepository.findTopByChallengeIdAndUserIdOrderByDateDesc(challengeId,userId).orElseThrow();
        return ChallengeVerificationResponse.from(cv);
    }

    public List<MembersVerificationCountResponse> getChallengeMemberVerificationCounts(Long challengeId) {

        return challengeVerificationRepository
                .countVerificationByChallengeMembers(challengeId);
    }


}
