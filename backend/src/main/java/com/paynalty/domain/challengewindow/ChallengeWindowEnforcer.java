package com.paynalty.domain.challengewindow;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengemember.ChallengeMemberRepository;
import com.paynalty.domain.challengeverification.ChallengeVerificationRepository;
import com.paynalty.domain.penalty.PenaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * ChallengeWindow 상태를 검증하고 벌금을 부과하는 스케줄러
 *
 * - 10분마다 실행 (KST 기준)
 * - PENDING 상태이고 인증 종료 시간이 지난 Window를 조회
 * - ChallengeVerification의 생성 시간이 인증 시간대(verifyStartAt ~ verifyEndAt) 안에 있는지 확인
 * - 시간대 안이면 SUCCESS, 시간대 밖이거나 없으면 FAIL
 * - FAIL인 경우 벌금 생성
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ChallengeWindowEnforcer {

    private final ChallengeWindowRepository challengeWindowRepository;
    private final ChallengeVerificationRepository challengeVerificationRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeMemberRepository challengeMemberRepository;
    private final PenaltyService penaltyService;

    /**
     * PENDING 상태이고 인증 종료 시간이 지난 ChallengeWindow를 검증합니다.
     * 10분마다 실행됩니다.
     */
    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul")
    @Transactional
    public void enforceWindows() {
        LocalDateTime now = LocalDateTime.now();
        
        // STEP1: PENDING 상태이고 인증 종료 시간이 지난 Window 조회
        List<ChallengeWindow> pendingWindows = challengeWindowRepository
                .findByChallengeWindowStatusAndChallengeWindowEndLessThanEqual(
                        ChallengeWindowStatus.PENDING,
                        now
                );

        if (pendingWindows.isEmpty()) {
            log.debug("처리할 PENDING Window가 없습니다.");
            return;
        }

        log.info("{}개의 PENDING Window를 검증합니다.", pendingWindows.size());

        // STEP2: 각 Window에 대해 인증 여부 확인 및 상태 업데이트
        for (ChallengeWindow window : pendingWindows) {
            processWindow(window);
        }
    }

    /**
     * 개별 ChallengeWindow를 처리합니다.
     * - ChallengeVerification 조회
     * - 인증 생성 시간이 인증 시간대(verifyStartAt ~ verifyEndAt) 안에 있는지 확인
     * - 시간대 안이면 SUCCESS, 시간대 밖이거나 없으면 FAIL
     * - FAIL인 경우 벌금 생성
     */
    private void processWindow(ChallengeWindow window) {
        Long challengeId = window.getChallengeId();
        Long tossId = window.getTossId();
        LocalDate windowDate = window.getChallengeWindowStart().toLocalDate();

        // Challenge 조회 (인증 시간대 확인용)
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalStateException(
                        "Challenge not found: " + challengeId));

        // 해당 날짜의 인증 데이터 조회
        Optional<com.paynalty.domain.challengeverification.ChallengeVerification> verificationOpt =
                challengeVerificationRepository.findByChallengeIdAndTossIdAndDate(
                        challengeId, tossId, windowDate);

        boolean isValidVerification = false;

        if (verificationOpt.isPresent()) {
            // 인증 데이터가 있는 경우, 생성 시간이 인증 시간대 안에 있는지 확인
            com.paynalty.domain.challengeverification.ChallengeVerification verification =
                    verificationOpt.get();
            
            LocalDateTime createdAt = verification.getCreatedAt();
            LocalTime verificationTime = createdAt.toLocalTime();
            LocalTime verifyStartAt = challenge.getVerifyStartAt();
            LocalTime verifyEndAt = challenge.getVerifyEndAt();

            // 인증 시간대가 설정되어 있고, 생성 시간이 시간대 안에 있는지 확인
            if (verifyStartAt != null && verifyEndAt != null) {
                isValidVerification = !verificationTime.isBefore(verifyStartAt) &&
                                     !verificationTime.isAfter(verifyEndAt);
                
                if (!isValidVerification) {
                    log.debug("Window ID {}: 인증 시간대 밖 - 생성 시간: {}, 인증 시간대: {} ~ {}",
                            window.getId(), verificationTime, verifyStartAt, verifyEndAt);
                }
            } else {
                // 인증 시간대가 설정되지 않은 경우, 인증 데이터만 있으면 유효
                isValidVerification = true;
            }
        }

        if (isValidVerification) {
            // 인증 데이터가 있고 시간대 안이면 SUCCESS로 변경
            window.setChallengeWindowStatus(ChallengeWindowStatus.SUCCESS);
            log.debug("Window ID {}: 인증 완료 → SUCCESS", window.getId());
        } else {
            // 인증 데이터가 없거나 시간대 밖이면 FAIL로 변경 및 벌금 생성
            window.setChallengeWindowStatus(ChallengeWindowStatus.FAIL);
            log.info("Window ID {}: 인증 실패 → FAIL (인증 데이터 없음 또는 시간대 밖)",
                    window.getId());
            
            createPenalty(window, challenge);
        }
    }

    /**
     * FAIL된 Window에 대해 벌금을 생성합니다.
     *
     * @param window FAIL된 ChallengeWindow
     * @param challenge Challenge 엔티티 (이미 조회된 경우)
     */
    private void createPenalty(ChallengeWindow window, Challenge challenge) {
        try {
            // penaltyAmount가 없으면 벌금 생성하지 않음
            if (challenge.getPenaltyAmount() == null || challenge.getPenaltyAmount() <= 0) {
                log.debug("Challenge ID {}: 벌금 금액이 설정되지 않아 벌금을 생성하지 않습니다.", 
                        challenge.getId());
                return;
            }

            // ChallengeMember 조회
            ChallengeMember member = challengeMemberRepository
                    .findByChallengeIdAndUserTossIdWithFetch(window.getChallengeId(), window.getTossId())
                    .orElseThrow(() -> new IllegalStateException(
                            String.format("ChallengeMember not found: challengeId=%d, tossId=%d",
                                    window.getChallengeId(), window.getTossId())));

            // 벌금 생성 (서비스를 통해 생성)
            penaltyService.create(member, challenge.getPenaltyAmount());
            log.info("벌금 생성 완료: Challenge ID {}, Toss ID {}, Amount {}",
                    challenge.getId(), window.getTossId(), challenge.getPenaltyAmount());

        } catch (Exception e) {
            log.error("벌금 생성 중 오류 발생: Window ID {}, Error: {}",
                    window.getId(), e.getMessage(), e);
            // 벌금 생성 실패해도 Window 상태는 이미 FAIL로 변경되었으므로 계속 진행
        }
    }
}