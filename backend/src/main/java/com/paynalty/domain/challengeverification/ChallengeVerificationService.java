package com.paynalty.domain.challengeverification;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeVerificationService {
    private final ChallengeVerificationRepository challengeVerificationRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    @Transactional
    public ChallengeVerificationResponse create(Long challengeId, Long userId, ChallengeVerificationRequest request) {
        // 챌린지 존재 확인
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // ChallengeVerification 생성
        ChallengeVerification challengeVerification = ChallengeVerification.builder()
                .user(user)
                .challenge(challenge)
                .imageUrl(request.getImageUrl())
                .status(request.getStatus())
                .build();

        ChallengeVerification saved = challengeVerificationRepository.save(challengeVerification);
        
        // Lazy Loading 문제 방지를 위해 트랜잭션 내에서 연관 엔티티 로드
        // Response 생성 시 user와 challenge에 접근하므로 미리 로드
        saved.getUser().getId(); // Lazy Loading 트리거
        saved.getChallenge().getId(); // Lazy Loading 트리거
        
        return ChallengeVerificationResponse.from(saved);
    }
}
