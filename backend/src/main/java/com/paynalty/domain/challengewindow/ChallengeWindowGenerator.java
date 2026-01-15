package com.paynalty.domain.challengewindow;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.challenge.ChallengeStatus;
import com.paynalty.domain.challenge.DayOfWeekType;
import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengemember.ChallengeMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ChallengeWindow를 미리 생성하는 스케줄러
 *
 * - 매일 1회 KST 00:00에 실행
 * - today부터 today + LOOKAHEAD_DAYS 까지의 기간을 대상으로 window 생성
 * - 두 가지 챌린지 타입을 모두 지원
 *
 *   1) 요일 기반 챌린지
 *      - 특정 요일에만 ChallengeWindow 생성
 *
 *   2) 주간 횟수 기반 챌린지 (N times per week)
 *      - ISO 주(월~일) 기준으로 해당 주의 모든 날짜(월~일)에 window 생성
 *      - 벌금은 남은 인증 횟수와 남은 일수를 비교하여 결정
 *
 * - 이미 생성된 ChallengeWindow는 재생성하지 않음 (멱등성 보장)
 */
@RequiredArgsConstructor
@Component
public class ChallengeWindowGenerator {

    // 오늘을 기준으로 며칠 앞까지 ChallengeWindow를 생성할지 정의
    // 예) 2 → today, tomorrow, day-after-tomorrow
    private final int LOOKAHEAD_DAYS = 2;

    private final ChallengeRepository challengeRepository;
    private final ChallengeMemberRepository challengeMemberRepository;
    private final ChallengeWindowRepository challengeWindowRepository;


//     * ChallengeWindow 생성
//     * [1] today - (today + LOOKAHEAD_DAYS) 기간과 겹치는 Challenge 조회
//     * [2] lookahead 기간과 관련 있는 Challenge만 후보로 필터링
//     *     - 요일 기반: lookahead 기간의 요일과 겹쳐야 함
//     *     - 횟수 기반: 요일 제한이 없으므로 모두 통과
//     * [3] 후보 Challenge에 속한 ChallengeMember 조회
//     * [4] 기존 ChallengeWindow 조회
//     *     - 멱등성 유지
//     *     - 주간 횟수 계산을 위해 ISO 주 단위 전체 범위 조회
//     * [5] 누락된 ChallengeWindow만 신규 생성
//     *
//     * ⚠️ 테스트용: cron을 "0 */1 * * * *"로 변경하면 1분마다 실행됩니다.
//     * 프로덕션 배포 전에는 "0 0 0 * * *"로 되돌려야 합니다.
//    @Scheduled(cron = "0 */1 * * * *", zone = "Asia/Seoul")  // 테스트용: 1분마다 실행
     @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")  // 프로덕션: 매일 자정 실행
    @Transactional
    public void generateWindows() {

        // [0단계] 모든 챌린지 조회 후 각 챌린지 status 업데이트
        // 1. PENDING, ACTIVE인 챌린지만 조회 (COMPLETE된 건 더 이상 업데이트할 필요가 없음)
        List<ChallengeStatus> statusesToUpdate = List.of(ChallengeStatus.PENDING, ChallengeStatus.ACTIVE);
        List<Challenge> challengeList = challengeRepository.findByStatusIn(statusesToUpdate);
        
        // 2. 리스트 목록 반복을 통해 각 챌린지 상태 업데이트
        for (Challenge challenge : challengeList) {
            challenge.updateStatus();
        }
        
        // 3. 업데이트된 챌린지들을 DB에 저장
        if (!challengeList.isEmpty()) {
            challengeRepository.saveAll(challengeList);
        }

        // [1단계] rolling lookahead 기간 설정
        // today : 스케줄 실행 기준일 (KST)
        // until : window 생성의 마지막 날짜
        LocalDate today = LocalDate.now();  // ex)2025-12-20
        LocalDate until = today.plusDays(LOOKAHEAD_DAYS); // ex)2025-12-22

        List<Challenge> activeChallengesSpanningPeriod = challengeRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, until);

        // [2단계] today ~ until 사이에 포함되는 요일 집합 계산
        // 요일 기반 챌린지를 미리 거르기 위한 용도
        Set<DayOfWeek> windowDaysOfWeek = new HashSet<>();
        for (LocalDate date = today; !date.isAfter(until); date = date.plusDays(1)) {
            windowDaysOfWeek.add(date.getDayOfWeek());
        }

        // 후보 Challenge 필터링 규칙
        // - 요일 기반 챌린지:
        //     → 설정된 요일 중 하나라도 lookahead 요일과 겹쳐야 함
        // - 횟수 기반 챌린지:
        //     → 요일 제한이 없으므로 무조건 후보로 포함
        List<Challenge> candidateChallenges =
                activeChallengesSpanningPeriod.stream()
                        .filter(challenge ->
                        {
                            // '요일(days of week)' 기반
                            if (challenge.getDaysOfWeek() != null && !challenge.getDaysOfWeek().isEmpty()) {
                                return challenge.getDaysOfWeek().stream()
                                        .map(DayOfWeekType::getDayOfWeek)
                                        .anyMatch(windowDaysOfWeek::contains);
                            }
                            // '횟수(times of week)' 기반
                            return true;
                        })
                        .toList();

        if (candidateChallenges.isEmpty()) return;

        // [3단계] 후보 Challenge에 속한 모든 ChallengeMember 조회
        // ChallengeWindow는 항상 (challenge + user) 단위로 생성됨
        List<ChallengeMember> challengeMembers = challengeMemberRepository.findWithUserAndChallengeIn(candidateChallenges);

        if (challengeMembers.isEmpty()) return;

        // [4단계] 기존 ChallengeWindow 조회
        // 중요: 주간 횟수 기반 챌린지는 "해당 ISO 주에 이미 몇 개의 window가 있는지"를 정확히 알아야 하므로 lookahead 기간만 조회하면 안 됨.
        // 따라서 조회 범위를 다음과 같이 확장함:
        // - today가 속한 ISO 주의 월요일
        // - until이 속한 ISO 주의 일요일

        // ISO 주 기준 (월요일 시작, 일요일 종료)
        WeekFields weekFields = WeekFields.ISO;
        // today가 속한 주의 월요일
        LocalDate firstWeekStart = today.with(weekFields.dayOfWeek(), 1);
        // until이 속한 주의 일요일
        LocalDate lastWeekEnd = until.with(weekFields.dayOfWeek(), 7);

        // 조회 범위:
        // [firstWeekStart 00:00, lastWeekEnd + 1일 00:00) → 마지막 주 일요일 23:59:59.999까지 포함
        LocalDateTime searchRangeStart = firstWeekStart.atStartOfDay();
        LocalDateTime searchRangeEnd = lastWeekEnd.plusDays(1).atStartOfDay();

        // 4-1. 유효한 (challenge_id, toss_id) 조합 생성 (ex. 1:1, 1:4, 2:5) (4-3에서 무관한 조합 걸러내는 용도)
        Set<String> validChallengeIdAndTossIdPairs =
                challengeMembers.stream()
                        .map(challengeMember -> challengeMember.getChallenge().getId() + ":" + challengeMember.getUser().getTossId())
                        .collect(Collectors.toSet());

        // [4-2] cartesian 조건으로 기존 ChallengeWindow 조회
        // challengeIds × tossIds × time range
        Set<Long> challengeIds =
                challengeMembers.stream()
                        .map(challengeMember -> challengeMember.getChallenge().getId())
                        .collect(Collectors.toSet());

        Set<Long> tossIds =
                challengeMembers.stream()
                        .map(challengeMember -> challengeMember.getUser().getTossId())
                        .collect(Collectors.toSet());

        List<ChallengeWindow> existingWindows = challengeWindowRepository.findByChallengeIdInAndTossIdInAndChallengeWindowStartBetween(
                                challengeIds,
                                tossIds,
                                searchRangeStart,
                                searchRangeEnd);

        // [4-3] 기존 ChallengeWindow의 lookup key 생성
        // 형식: challengeId:tossId:challengeWindowStart → 동일 window 중복 생성 방지
        Set<String> existingWindowLookupKeys = existingWindows.stream()
                        // (1) 무관한 (challenge_id, toss_id) 제거
                        .filter(existingWindow -> validChallengeIdAndTossIdPairs.contains(existingWindow.getChallengeId() + ":" + existingWindow.getTossId()))
                        // (2) deduplicate된 lookup key 생성
                        .map(window -> window.getChallengeId() + ":" + window.getTossId() + ":" + window.getChallengeWindowStart())
                        .collect(Collectors.toSet());

        // [4-4] ISO 주 단위 ChallengeWindow 개수 집계
        // key 형식: challengeId:tossId:weekBasedYear:weekOfWeekBasedYear
        // 횟수 기반 챌린지에서 "이번 주에 이미 몇 번 생성되었는지" 판단하는 데 사용
        Map<String, Long> weeklyWindowCount =
                existingWindows.stream()
                        .collect(Collectors.groupingBy(
                                w -> w.getChallengeId()
                                        + ":" + w.getTossId()
                                        + ":" + w.getChallengeWindowStart()
                                        .get(weekFields.weekBasedYear())
                                        + ":" + w.getChallengeWindowStart()
                                        .get(weekFields.weekOfWeekBasedYear()),
                                Collectors.counting()
                        ));

        // [5단계] 누락된 ChallengeWindow 생성
        List<ChallengeWindow> windowsToGenerate = new ArrayList<>();

        for (ChallengeMember member : challengeMembers) {

            Challenge challenge = member.getChallenge();
            Long challengeId = challenge.getId();
            Long tossId = member.getUser().getTossId();
            LocalDate memberJoinedDate = member.getJoinedAt().toLocalDate();

            // 챌린지 타입 판별
            // - isDaysOfWeekBased == true  → 요일 기반
            //                     == false → 주간 횟수 기반
            boolean isDaysOfWeekBased =
                    challenge.getDaysOfWeek() != null && !challenge.getDaysOfWeek().isEmpty();

            Set<DayOfWeek> challengeDays =
                    isDaysOfWeekBased
                            ? challenge.getDaysOfWeek().stream()
                            .map(DayOfWeekType::getDayOfWeek)
                            .collect(Collectors.toSet())
                            : Collections.emptySet();

            // 주간 횟수 기반 챌린지의 경우: 해당 주의 모든 날짜(월~일) 생성
            // 요일 기반 챌린지의 경우: today부터 until까지 순회하며 설정된 요일에만 생성
            LocalDate startDate = isDaysOfWeekBased ? today : today.with(weekFields.dayOfWeek(), 1); // 주간 횟수 기반이면 해당 주의 월요일부터
            LocalDate endDate = isDaysOfWeekBased ? until : today.with(weekFields.dayOfWeek(), 7); // 주간 횟수 기반이면 해당 주의 일요일까지

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {

                // 5-1. 다음 조건 중 하나라도 해당되면 생성하지 않음
                // - 챌린지 기간 밖
                // - 멤버가 아직 참여하지 않은 날짜
                // - 주간 횟수 기반인 경우: lookahead 기간 밖 (오늘 이전 날짜는 생성하지 않음)
                if (date.isBefore(challenge.getStartDate()) || date.isAfter(challenge.getEndDate()) || date.isBefore(memberJoinedDate)) continue;
                if (!isDaysOfWeekBased && date.isBefore(today)) continue; // 주간 횟수 기반: 오늘 이전 날짜는 생성하지 않음

                // 5-2. 챌린지 타입별 생성 조건
                // 요일 기반: date의 요일이 설정된 요일에 포함되어야 함
                // 횟수 기반: 7개 모두 생성 (제한 없음)
                if (isDaysOfWeekBased) {
                    if (!challengeDays.contains(date.getDayOfWeek())) continue;
                }
                // 주간 횟수 기반 챌린지는 frequency 제한 없이 해당 주의 모든 날짜(월~일) 생성

                LocalDateTime challengeWindowStart = date.atTime(challenge.getVerifyStartAt());
                LocalDateTime challengeWindowEnd = date.atTime(challenge.getVerifyEndAt());

                // ChallengeWindow가 이미 존재하는지 확인하기 위한 고유 식별자 생성
                String lookupKey = challengeId + ":" + tossId + ":" + challengeWindowStart;
                // 5-3. 이미 동일한 ChallengeWindow가 존재하면 생성하지 않음
                if (existingWindowLookupKeys.contains(lookupKey)) continue;

                windowsToGenerate.add(
                        ChallengeWindow.builder()
                                .challengeId(challengeId)
                                .tossId(tossId)
                                .challengeWindowStart(challengeWindowStart)
                                .challengeWindowEnd(challengeWindowEnd)
                                .challengeWindowStatus(ChallengeWindowStatus.PENDING)
                                .build()
                );

            }
        }

        // 신규 ChallengeWindow가 있는 경우 bulk insert
        if (!windowsToGenerate.isEmpty()) challengeWindowRepository.saveAll(windowsToGenerate);

    }
}
