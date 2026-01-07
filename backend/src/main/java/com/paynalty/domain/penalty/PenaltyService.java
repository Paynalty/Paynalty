package com.paynalty.domain.penalty;

import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengemember.ChallengeMemberRepository;
import com.paynalty.global.error.ChallengeMemberErrorCode;
import com.paynalty.global.error.CustomException;
import com.paynalty.global.error.PenaltyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final ChallengeMemberRepository challengeMemberRepository;

    /**
     * 본인 벌금 내역 조회
     *
     * @param challengeId 챌린지 ID
     * @param tossId 사용자 토스 ID
     * @return 벌금 내역 리스트
     */
    @Transactional(readOnly = true)
    public List<PenaltyResponse> getMyPenalty(Long challengeId, Long tossId) {
        List<Penalty> penalties = penaltyRepository.findByChallengeIdAndTossId(challengeId, tossId);
        return penalties.stream()
                .map(PenaltyResponse::from)
                .toList();
    }

    /**
     * ChallengeMember와 금액을 직접 받아 벌금을 생성합니다.
     * (내부 서비스 간 호출용)
     *
     * @param challengeMember 챌린지 멤버
     * @param amount 벌금 금액
     * @return 생성된 벌금
     */
    @Transactional
    public Penalty create(ChallengeMember challengeMember, Long amount) {
        // 벌금 금액 검증
        if (amount == null || amount <= 0) {
            throw new CustomException(PenaltyErrorCode.INVALID_PENALTY_AMOUNT);
        }

        // 벌금 생성
        Penalty penalty = Penalty.builder()
                .challengeMember(challengeMember)
                .fixedAmount(amount)
                .build();

        return penaltyRepository.save(penalty);
    }
}

