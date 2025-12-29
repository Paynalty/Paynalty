package com.paynalty.domain.challengewindow;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ChallengeWindowGenerator {

    private final int LOOKAHEAD_DAYS = 2;

    private final ChallengeRepository challengeRepository;
    private final ChallengeMemberRepository challengeMemberRepository;
    private final com.paynalty.domain.challengewindow.ChallengeWindowRepository challengeWindowRepository;

    // today부터 today+LOOKAHEAD_DAYS까지의 ChallengeWindow 생성
    // 매일 1회, KST 00:00:00.000에 실행

    // 1단계: Challenge 중에서 (1)start_date가 today와 같거나 이르고, (2)end_date가 until과 같거나 후인 모든 records 확인
    // 2단계: Challenge에 설정된 요일이 today부터 until에 포함된 요일과 일치하는지 확인
    // 3단계: 1,2단계에서 필터링된 Challenge에 해당하는 모든 ChallengeMember records 확인
    // 4단계: 3단계에서 얻은 ChallengeMember records의 challenge_id, user_id, start_at으로 기존 ChallengeWindow records 확인 (멱등성 idempotency 유지 목적)
    // 5단계: 4단계에서 존재하지 않는 ChallengeWindow 신규 생성
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void generateWindows() {

        // [1단계]
        LocalDate today = LocalDate.now();  // ex)2025-12-20
        LocalDate until = today.plusDays(LOOKAHEAD_DAYS); // ex)2025-12-22

        List<Challenge> activeChallengesSpanningPeriod = challengeRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, until);

        // [2단계]
        Set<DayOfWeek> windowDaysOfWeek = new HashSet<>();
        for (LocalDate date = today; !date.isAfter(until); date = date.plusDays(1)) {
            windowDaysOfWeek.add(date.getDayOfWeek());
        }

        List<Challenge> matchingDaysOfWeekChallenges =
                activeChallengesSpanningPeriod.stream()
                        .filter(challenge ->
                                challenge.getDaysOfWeek() != null &&
                                        challenge.getDaysOfWeek().stream()
                                                .map(DayOfWeekType::getDayOfWeek)
                                                .anyMatch(windowDaysOfWeek::contains)
                        )
                        .toList();

        if (matchingDaysOfWeekChallenges.isEmpty()) return;

        // [3단계]
        // TODO: ADD WINDOW GENERATION LOGIC WHEN A NEW MEMBER JOINS AN EXISTING CHALLENGE - ALIGN WITH 재현님
        List<ChallengeMember> challengeMembers = challengeMemberRepository.findWithUserAndChallengeIn(matchingDaysOfWeekChallenges);

        if (challengeMembers.isEmpty()) return;

        // [4단계]
        LocalDateTime searchRangeStart = today.atStartOfDay(); // ex)2025-12-20T00:00 즉, 20일 시작부터 포함 (19일 제외)
        LocalDateTime searchRangeEnd = until.plusDays(1).atStartOfDay(); // ex)2025-12-22 → 2025-12-23T00:00 직전까지 == 2025-12-22T23:59:59.999... 즉, 22일 끝까지 포함 (23일 제외)

        // 4-1. (challenge_id, user_id) 조합 생성 (ex. 1:1, 1:4, 2:5) (4-3에서 무관한 조합 걸러내는 용도)
        Set<String> validChallengeIdAndUserIdPairs =
                challengeMembers.stream()
                        .map(challengeMember -> challengeMember.getChallenge().getId() + ":" + challengeMember.getUser().getId())
                        .collect(Collectors.toSet());

        // 4-2. (challenge_id, user_id) cartesian production으로 DB 1회 호출 ex)challengeIds=[1,2] userIds=[1,4,5] => 1:1, 1:4, 1:5, 2:1, 2:4, 2:5
        Set<Long> challengeIds =
                challengeMembers.stream()
                        .map(challengeMember -> challengeMember.getChallenge().getId())
                        .collect(Collectors.toSet());

        Set<Long> userIds =
                challengeMembers.stream()
                        .map(challengeMember -> challengeMember.getUser().getId())
                        .collect(Collectors.toSet());

        List<ChallengeWindow> existingWindows = challengeWindowRepository.findByChallengeIdInAndUserIdInAndChallengeWindowStartBetween(
                                challengeIds,
                                userIds,
                                searchRangeStart,
                                searchRangeEnd);

        // 4-3. 유효한 (challenge_id, user_id) 조합만 남기고 challenge_id:user_id:challenge_window_start lookup key 생성
        Set<String> existingWindowLookupKeys = existingWindows.stream()
                        // (1) 무관한 (challenge_id, user_id) 제거
                        .filter(existingWindow -> validChallengeIdAndUserIdPairs.contains(existingWindow.getChallengeId() + ":" + existingWindow.getUserId()))
                        // (2) deduplicate된 lookup key 생성
                        .map(window -> window.getChallengeId() + ":" + window.getUserId() + ":" + window.getChallengeWindowStart())
                        .collect(Collectors.toSet());

        // [5단계]
        List<ChallengeWindow> windowsToGenerate = new ArrayList<>();

        for (ChallengeMember member : challengeMembers) {

            Challenge challenge = member.getChallenge();
            Long challengeId = challenge.getId();
            Long userId = member.getUser().getId();
            LocalDate memberJoinedDate = member.getJoinedAt().toLocalDate();

            // Challenge에 설정된 요일 목록을 Set<DayOfWeek>로 변환
            Set<DayOfWeek> challengeDays = challenge.getDaysOfWeek().stream()
                            .map(DayOfWeekType::getDayOfWeek)
                            .collect(Collectors.toSet());

            // today부터 until(today+LOOKAHEAD_DAYS)까지 반복
            // date: 해당 ChallengeWindow가 생성될 기준 일자
            for (LocalDate date = today; !date.isAfter(until); date = date.plusDays(1)) {

                // 5-1. 기준 일자가 (1)챌린지 기간에 미포함 혹은 (2)멤버가 참여하기 이전의 일자면 window 미생성
                if (date.isBefore(challenge.getStartDate()) || date.isAfter(challenge.getEndDate()) || date.isBefore(memberJoinedDate)) continue;

                // 5-2. 기준 일자의 요일이 챌린지 요일 설정에 포함되지 않으면 window 미생성
                if (!challengeDays.contains(date.getDayOfWeek())) continue;

                LocalDateTime challengeWindowStart = date.atTime(challenge.getVerifyStartAt());
                LocalDateTime challengeWindowEnd = date.atTime(challenge.getVerifyEndAt());

                // ChallengeWindow가 이미 존재하는지 확인하기 위한 고유 식별자 생성
                String lookupKey = challengeId + ":" + userId + ":" + challengeWindowStart;
                // 5-3. 기존의 ChallengeWindow 고유 식별자 Set에서 검색
                if (existingWindowLookupKeys.contains(lookupKey)) continue;

                windowsToGenerate.add(
                        ChallengeWindow.builder()
                                .challengeId(challengeId)
                                .userId(userId)
                                .challengeWindowStart(challengeWindowStart)
                                .challengeWindowEnd(challengeWindowEnd)
                                .challengeWindowStatus(ChallengeWindowStatus.PENDING)
                                .build()
                );
            }
        }

        // 신규 ChallengeWindow가 있는 경우 bulk insert
        if (!windowsToGenerate.isEmpty()) {
            challengeWindowRepository.saveAll(windowsToGenerate);
        }

    }
}
