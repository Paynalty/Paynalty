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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final ChallengeMemberRepository challengeMemberRepository;


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
                .penaltyAmount(amount)
                .build();

        return penaltyRepository.save(penalty);
    }

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
     * 챌린지의 모든 벌금 내역 조회
     * 사용자가 해당 챌린지에 참여하고 있는지 검증 후 모든 벌금 내역을 반환합니다.
     *
     * @param challengeId 챌린지 ID
     * @param tossId 사용자 토스 ID (검증용)
     * @return 벌금 내역 리스트 (최신순 정렬)
     * @throws CustomException 챌린지에 참여하지 않은 경우
     */
    @Transactional(readOnly = true)
    public List<PenaltyResponse> getAllPenalties(Long challengeId, Long tossId) {
        // 사용자가 해당 챌린지에 참여하는지 검증
        ChallengeMember member = challengeMemberRepository
                .findByChallengeIdAndUserTossIdWithFetch(challengeId, tossId)
                .orElseThrow(() -> new CustomException(ChallengeMemberErrorCode.CHALLENGE_MEMBER_NOT_FOUND));

        // 해당 챌린지의 모든 벌금 내역 조회
        List<Penalty> penalties = penaltyRepository.findAllByChallengeId(challengeId);
        
        return penalties.stream()
                .map(PenaltyResponse::from)
                .toList();
    }

    /**
     * 패널티 결제 완료 처리
     * 패널티의 paid 필드를 true로 변경하고, paidAt을 현재 시간으로 설정합니다.
     *
     * @param penaltyId 패널티 ID
     * @param tossId 사용자 토스 ID (권한 검증용)
     * @return 결제 완료된 패널티 응답
     * @throws CustomException 패널티가 없거나, 이미 결제되었거나, 권한이 없는 경우
     */
    @Transactional
    public PenaltyResponse paidComplete(Long penaltyId, Long tossId) {
        // penaltyId로 패널티 찾기
        Penalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new CustomException(PenaltyErrorCode.PENALTY_NOT_FOUND));

        // 해당 penalty의 paid가 이미 true면 오류코드. 이미 결제 되었습니다 표시
        if (penalty.getPaid()) {
            throw new CustomException(PenaltyErrorCode.PENALTY_ALREADY_PAID);
        }

        // 해당 패널티의 챌린지 멤버 -> 사용자 tossId가 현재 로그인한 사용자의 토스 id와 같은지 검증
        if (!penalty.getChallengeMember().getUser().getTossId().equals(tossId)) {
            // 같지 않다면 '사용자가 해당 패널티의 권한이 없습니다' 오류코드 전달
            throw new CustomException(PenaltyErrorCode.UNAUTHORIZED_PENALTY_ACCESS);
        }

        // 같으면 해당 패널티의 paid true로 변경, 결제시간을 현재로 수정
        penalty.setPaid(true);
        penalty.setPaidAt(java.time.LocalDateTime.now());

        // 저장소 쿼리 실행
        Penalty savedPenalty = penaltyRepository.save(penalty);

        return PenaltyResponse.from(savedPenalty);
    }
}

