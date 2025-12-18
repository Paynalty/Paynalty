package com.paynalty.domain.challengeverification;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.challenge.ChallengeResponse;
import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChallengeVerificationService {
    private final ChallengeVerificationRepository challengeVerificationRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    public ChallengeVerificationResponse create(Long challengeId,Long userId, ChallengeVerificationRequest request){
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        ChallengeVerification challengeVerification = ChallengeVerification.builder()
                .user(user)
                .challenge(challenge)
                .imageUrl(request.getImageUrl())
                .status(request.getStatus())
                .build();

        ChallengeVerification saved = challengeVerificationRepository.save(challengeVerification);
        return ChallengeVerificationResponse.from(saved);

    }

}
