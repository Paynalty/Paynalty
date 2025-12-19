package com.paynalty.domain.challenge;

import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserRepository;
import com.paynalty.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChallengeResponse create(ChallengeRequest request,Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        Challenge challenge = Challenge.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .frequency(request.getFrequency())
                .penaltyAmount(request.getPenaltyAmount())
                .status(request.getStatus())
                .user(user)
                .verificationType(request.getVerificationType())
                .verifyStartAt(request.getVerifyStartAt())
                .verifyEndAt(request.getVerifyEndAt())
                .build();

        Challenge saved = challengeRepository.save(challenge);

        // challenge 생성됨. (challenge id 값 있음)
        // challenge -> challengeMembers 에 맴버 추가
        // challengeMembers 안의 인덱스 값만큼 ChallengeMember 객체 생성 매서드 실행
        // (객체 생성 -> challenge의 Members 리스트에 추가)


        return ChallengeResponse.from(saved);
    }


    public List<ChallengeResponse> findByStatus(Long userId,String status){
        // status 상태,사용자가 참여 중인 : 조건에 맞는 challenge 불러오기
        List<Challenge> challenges = challengeRepository.findByEmailAndStatus(userId,status);

        return challenges.stream().map(ChallengeResponse::from).collect(Collectors.toList());

    }
}

