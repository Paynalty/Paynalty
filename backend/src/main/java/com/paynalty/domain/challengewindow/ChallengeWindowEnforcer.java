package com.paynalty.domain.challengewindow;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengemember.ChallengeMemberRepository;
import com.paynalty.domain.challengeverification.ChallengeVerificationRepository;
import com.paynalty.domain.penalty.PenaltyRepository;
import com.paynalty.domain.penalty.PenaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ChallengeWindow 상태를 검증하고 벌금을 부과하는 스케줄러
 *
 * - 10분마다 실행 (KST 기준)
 * - PENDING 상태이고 인증 종료 시간이 지난 Window를 조회
 * 
 * 검증 로직:
 * 1. 요일 기반 챌린지 (daysOfWeek가 있는 경우):
 *    - 해당 날짜의 인증 데이터 확인
 *    - 인증 생성 시간이 인증 시간대(verifyStartAt ~ verifyEndAt) 안에 있는지 확인
 *    - 시간대 안이면 SUCCESS, 시간대 밖이거나 없으면 FAIL
 *    - FAIL인 경우 벌금 생성
 * 
 * 2. 주간 횟수 기반 챌린지 (daysOfWeek가 null이거나 비어있는 경우):
 *    - 해당 주의 총 인증 횟수 확인
 *    - 남은 인증 횟수 = frequency - 현재 인증 횟수
 *    - 남은 일수 = 오늘부터 일요일까지 (오늘 인증 안 했다고 가정)
 *    - 남은 인증 횟수 > 남은 일수이면 벌금 생성 (부족한 횟수만큼)
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
    private final PenaltyRepository penaltyRepository;

    /**
     * PENDING 상태이고 인증 종료 시간이 지난 ChallengeWindow를 검증합니다.
     * 10분마다 실행됩니다.
     */
    @Scheduled(cron = "0 */1 * * * *", zone = "Asia/Seoul")
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

        // STEP2: 챌린지 타입별로 그룹화
        Map<Boolean, List<ChallengeWindow>> windowsByType = pendingWindows.stream()
                .collect(Collectors.partitioningBy(window -> {
                    Challenge challenge = challengeRepository.findById(window.getChallengeId())
                            .orElse(null);
                    return challenge != null 
                            && challenge.getDaysOfWeek() != null 
                            && !challenge.getDaysOfWeek().isEmpty();
                }));

        // STEP3: 요일 기반 챌린지 Window 처리 (기존 로직)
        List<ChallengeWindow> dayBasedWindows = windowsByType.get(true);
        if (dayBasedWindows != null) {
            for (ChallengeWindow window : dayBasedWindows) {
                processDayBasedWindow(window);
            }
        }

        // STEP4: 주간 횟수 기반 챌린지 Window 처리 (새로운 로직)
        // 각 Window를 개별적으로 처리하여 매일 인증 마감 시간마다 벌금 생성
        List<ChallengeWindow> frequencyBasedWindows = windowsByType.get(false);
        if (frequencyBasedWindows != null && !frequencyBasedWindows.isEmpty()) {
            for (ChallengeWindow window : frequencyBasedWindows) {
                processFrequencyBasedWindow(window);
            }
        }
    }

    /**
     * 요일 기반 챌린지의 개별 ChallengeWindow를 처리합니다.
     * - ChallengeVerification 조회
     * - 인증 생성 시간이 인증 시간대(verifyStartAt ~ verifyEndAt) 안에 있는지 확인
     * - 시간대 안이면 SUCCESS, 시간대 밖이거나 없으면 FAIL
     * - FAIL인 경우 벌금 생성
     */
    private void processDayBasedWindow(ChallengeWindow window) {
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
            // 인증 데이터가 있는 경우, 인증 시간이 인증 시간대 안에 있는지 확인
            com.paynalty.domain.challengeverification.ChallengeVerification verification =
                    verificationOpt.get();
            
            // verifiedAt이 있으면 사용, 없으면 createdAt 사용
            LocalDateTime verificationDateTime = verification.getVerifiedAt() != null 
                    ? verification.getVerifiedAt() 
                    : verification.getCreatedAt();
            LocalTime verificationTime = verificationDateTime.toLocalTime();
            LocalTime verifyStartAt = challenge.getVerifyStartAt();
            LocalTime verifyEndAt = challenge.getVerifyEndAt();

            // 인증 시간대가 설정되어 있고, 인증 시간이 시간대 안에 있는지 확인
            if (verifyStartAt != null && verifyEndAt != null) {
                isValidVerification = !verificationTime.isBefore(verifyStartAt) &&
                                     !verificationTime.isAfter(verifyEndAt);
                
                if (!isValidVerification) {
                    log.debug("Window ID {}: 인증 시간대 밖 - 인증 시간: {}, 인증 시간대: {} ~ {}",
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
     * 주간 횟수 기반 챌린지의 개별 Window를 처리합니다.
     * 매일 인증 마감 시간이 지날 때마다 남은 인증 횟수와 남은 일수를 비교하여 벌금을 생성합니다.
     * 
     * 로직:
     * 1. 해당 날짜에 인증이 있고 인증 시간대 안에 있으면 SUCCESS로 처리
     * 2. 해당 Window의 날짜 기준으로 남은 인증 횟수와 남은 일수 계산
     * 3. 남은 인증 횟수 > 남은 일수이면 필요한 벌금 개수 계산
     * 4. 해당 주에 이미 생성된 벌금 개수 확인
     * 5. 필요한 벌금 개수 - 이미 생성된 벌금 개수 = 추가로 생성할 벌금 개수
     * 6. 추가 벌금이 있으면 생성하고 해당 날짜의 Window를 FAIL로 처리
     */
    private void processFrequencyBasedWindow(ChallengeWindow window) {
        Long challengeId = window.getChallengeId();
        Long tossId = window.getTossId();
        LocalDate windowDate = window.getChallengeWindowStart().toLocalDate();

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalStateException(
                        "Challenge not found: " + challengeId));

        // STEP 1: 해당 날짜의 인증 데이터 조회 및 인증 시간대 확인
        Optional<com.paynalty.domain.challengeverification.ChallengeVerification> verificationOpt =
                challengeVerificationRepository.findByChallengeIdAndTossIdAndDate(
                        challengeId, tossId, windowDate);

        boolean isValidVerification = false;

        if (verificationOpt.isPresent()) {
            // 인증 데이터가 있는 경우, 인증 시간이 인증 시간대 안에 있는지 확인
            com.paynalty.domain.challengeverification.ChallengeVerification verification =
                    verificationOpt.get();
            
            // verifiedAt이 있으면 사용, 없으면 createdAt 사용
            LocalDateTime verificationDateTime = verification.getVerifiedAt() != null 
                    ? verification.getVerifiedAt() 
                    : verification.getCreatedAt();
            LocalTime verificationTime = verificationDateTime.toLocalTime();
            LocalTime verifyStartAt = challenge.getVerifyStartAt();
            LocalTime verifyEndAt = challenge.getVerifyEndAt();

            // 인증 시간대가 설정되어 있고, 인증 시간이 시간대 안에 있는지 확인
            if (verifyStartAt != null && verifyEndAt != null) {
                isValidVerification = !verificationTime.isBefore(verifyStartAt) &&
                                     !verificationTime.isAfter(verifyEndAt);
                
                if (!isValidVerification) {
                    log.debug("주간 횟수 기반 Window ID {}: 인증 시간대 밖 - 인증 시간: {}, 인증 시간대: {} ~ {}",
                            window.getId(), verificationTime, verifyStartAt, verifyEndAt);
                }
            } else {
                // 인증 시간대가 설정되지 않은 경우, 인증 데이터만 있으면 유효
                isValidVerification = true;
            }
        }

        // 해당 날짜에 인증 시간대 안에 인증이 있으면 SUCCESS로 처리하고 종료
        if (isValidVerification) {
            window.setChallengeWindowStatus(ChallengeWindowStatus.SUCCESS);
            log.debug("주간 횟수 기반 Window ID {}: 해당 날짜 인증 완료 → SUCCESS", window.getId());
            return;
        }

        // STEP 2: 인증이 없거나 시간대 밖인 경우, 벌금 계산 로직 실행
        // 해당 Window가 속한 주의 시작일(월요일)과 종료일(일요일) 계산
        LocalDate weekStart = windowDate.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6); // 일요일

        // 해당 주의 총 인증 횟수 조회
        Long currentWeeklyCount = challengeVerificationRepository.countWeeklyVerifications(
                challengeId, tossId, weekStart, weekEnd);

        // 시작 주와 마지막 주의 경우 실제 인증 가능 일수를 기준으로 frequency 조정
        Integer requiredFrequency = calculateAdjustedFrequency(challenge, weekStart, weekEnd);
        int remainingVerifications = requiredFrequency - currentWeeklyCount.intValue();

        // 남은 일수 계산: 해당 날짜 다음날부터 일요일 또는 챌린지 종료일 중 더 빠른 날까지
        // 예: 목요일 Window → 금, 토, 일 = 3일
        // 예: 금요일 Window → 토, 일 = 2일
        // 예: 챌린지가 금요일에 종료되는 경우 → 남은 일수는 0일
        long remainingDays = 0;
        LocalDate nextDay = windowDate.plusDays(1);
        LocalDate challengeEndDate = challenge.getEndDate();
        
        // 실제 남은 일수는 일요일과 챌린지 종료일 중 더 빠른 날까지
        LocalDate actualEndDate = weekEnd.isBefore(challengeEndDate) || weekEnd.isEqual(challengeEndDate) 
                ? weekEnd 
                : challengeEndDate;
        
        if (nextDay.isBefore(actualEndDate) || nextDay.isEqual(actualEndDate)) {
            // 다음날부터 실제 종료일까지
            remainingDays = java.time.temporal.ChronoUnit.DAYS.between(nextDay, actualEndDate) + 1;
        }

        log.debug("주간 횟수 기반 챌린지 Window 검증 - Window ID: {}, 날짜: {}, " +
                        "현재 인증: {}, 조정된 필요 횟수: {}, 남은 인증: {}, 남은 일수: {}",
                window.getId(), windowDate, currentWeeklyCount, requiredFrequency,
                remainingVerifications, remainingDays);

        // 남은 인증 횟수 > 남은 일수이면 벌금 필요
        if (remainingVerifications > remainingDays && remainingDays >= 0) {
            int requiredPenaltyCount = remainingVerifications - (int) remainingDays;

            // 해당 주에 이미 생성된 벌금 개수 확인
            Long existingPenaltyCount = penaltyRepository.countWeeklyPenalties(
                    challengeId, tossId, weekStart, weekEnd);

            int additionalPenaltyCount = requiredPenaltyCount - existingPenaltyCount.intValue();

            if (additionalPenaltyCount > 0) {
                log.info("주간 횟수 기반 챌린지 벌금 발생 - Window ID: {}, 날짜: {}, " +
                                "필요한 벌금: {}, 이미 생성된 벌금: {}, 추가 생성할 벌금: {}",
                        window.getId(), windowDate, requiredPenaltyCount, existingPenaltyCount, additionalPenaltyCount);

                ChallengeMember member = challengeMemberRepository
                        .findByChallengeIdAndUserTossIdWithFetch(challengeId, tossId)
                        .orElseThrow(() -> new IllegalStateException(
                                String.format("ChallengeMember not found: challengeId=%d, tossId=%d",
                                        challengeId, tossId)));

                // 추가로 필요한 벌금만 생성
                for (int i = 0; i < additionalPenaltyCount; i++) {
                    createPenaltyForFrequencyBased(member, challenge);
                }

                // 해당 날짜의 Window를 FAIL로 처리
                window.setChallengeWindowStatus(ChallengeWindowStatus.FAIL);
            } else {
                // 이미 필요한 벌금이 모두 생성되었으므로 Window만 FAIL로 처리
                window.setChallengeWindowStatus(ChallengeWindowStatus.FAIL);
                log.debug("주간 횟수 기반 챌린지 - Window ID: {}, 날짜: {}, " +
                                "이미 필요한 벌금이 모두 생성됨 (필요: {}, 생성됨: {})",
                        window.getId(), windowDate, requiredPenaltyCount, existingPenaltyCount);
            }
        } else if (currentWeeklyCount >= requiredFrequency) {
            // 주간 인증 횟수 충족 시 SUCCESS로 처리
            window.setChallengeWindowStatus(ChallengeWindowStatus.SUCCESS);
            log.debug("주간 횟수 기반 챌린지 인증 완료 - Window ID: {}, 날짜: {}",
                    window.getId(), windowDate);
        }
        // 남은 인증 횟수 <= 남은 일수이면 아직 기회가 있으므로 Window 상태 유지 (PENDING)
    }

    /**
     * 시작 주와 마지막 주의 경우 실제 인증 가능 일수 비율에 따라 frequency를 조정합니다.
     * 
     * 공식: 조정된 frequency = floor(원래 frequency × (실제 인증 가능 일수 / 7))
     * 
     * 예시:
     * - 주 4회 인증 챌린지가 토요일에 시작: 4 × (2/7) = 1.14... → 1회
     * - 주 5회 인증 챌린지가 목요일에 시작: 5 × (4/7) = 2.857... → 2회
     * - 주 4회 인증 챌린지가 화요일에 종료: 4 × (2/7) = 1.14... → 1회
     * 
     * @param challenge 챌린지
     * @param weekStart 해당 주의 시작일 (월요일)
     * @param weekEnd 해당 주의 종료일 (일요일)
     * @return 조정된 frequency
     */
    private Integer calculateAdjustedFrequency(Challenge challenge, LocalDate weekStart, LocalDate weekEnd) {
        LocalDate challengeStartDate = challenge.getStartDate();
        LocalDate challengeEndDate = challenge.getEndDate();
        Integer originalFrequency = challenge.getFrequency();

        // 시작 주인지 확인
        boolean isStartWeek = !challengeStartDate.isAfter(weekEnd) && !challengeStartDate.isBefore(weekStart);
        
        // 마지막 주인지 확인
        boolean isEndWeek = !challengeEndDate.isBefore(weekStart) && !challengeEndDate.isAfter(weekEnd);

        // 일반 주인 경우 원래 frequency 반환
        if (!isStartWeek && !isEndWeek) {
            return originalFrequency;
        }

        // 시작 주인 경우: 챌린지 시작일부터 해당 주의 일요일까지의 실제 일수 계산
        LocalDate effectiveStart = isStartWeek ? challengeStartDate : weekStart;
        
        // 마지막 주인 경우: 해당 주의 월요일부터 챌린지 종료일까지의 실제 일수 계산
        LocalDate effectiveEnd = isEndWeek ? challengeEndDate : weekEnd;

        // 실제 인증 가능 일수 계산
        long actualDays = java.time.temporal.ChronoUnit.DAYS.between(effectiveStart, effectiveEnd) + 1;

        // 전체 주의 일수 (7일)
        final int totalWeekDays = 7;

        // 비율에 따라 frequency 조정: floor(원래 frequency × (실제 일수 / 7))
        double ratio = (double) actualDays / totalWeekDays;
        double adjustedFrequencyDouble = originalFrequency * ratio;
        int adjustedFrequency = (int) Math.floor(adjustedFrequencyDouble);

        // 최소값은 1회 (0회가 되지 않도록)
        adjustedFrequency = Math.max(1, adjustedFrequency);

        log.debug("주간 횟수 기반 챌린지 frequency 조정 - 시작 주: {}, 마지막 주: {}, " +
                        "실제 일수: {}, 전체 일수: {}, 비율: {}, 원래 frequency: {}, 조정된 frequency: {}",
                isStartWeek, isEndWeek, actualDays, totalWeekDays, ratio, originalFrequency, adjustedFrequency);

        return adjustedFrequency;
    }

    /**
     * 주간 횟수 기반 챌린지의 벌금을 생성합니다.
     */
    private void createPenaltyForFrequencyBased(ChallengeMember member, Challenge challenge) {
        try {
            // penaltyAmount가 없으면 벌금 생성하지 않음
            if (challenge.getPenaltyAmount() == null || challenge.getPenaltyAmount() <= 0) {
                log.debug("Challenge ID {}: 벌금 금액이 설정되지 않아 벌금을 생성하지 않습니다.",
                        challenge.getId());
                return;
            }

            // 벌금 생성 (서비스를 통해 생성)
            penaltyService.create(member, challenge.getPenaltyAmount());
            log.info("주간 횟수 기반 챌린지 벌금 생성 완료: Challenge ID {}, Toss ID {}, Amount {}",
                    challenge.getId(), member.getUser().getTossId(), challenge.getPenaltyAmount());

        } catch (Exception e) {
            log.error("주간 횟수 기반 챌린지 벌금 생성 중 오류 발생: Challenge ID {}, Error: {}",
                    challenge.getId(), e.getMessage(), e);
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