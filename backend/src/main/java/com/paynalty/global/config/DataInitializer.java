/*

package com.paynalty.global.config;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.challenge.DayOfWeekType;
import com.paynalty.domain.challenge.VerificationType;
import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengemember.ChallengeMemberRepository;
import com.paynalty.domain.challengemember.MemberRole;
import com.paynalty.domain.challengeverification.ChallengeVerification;
import com.paynalty.domain.challengeverification.ChallengeVerificationRepository;
import com.paynalty.domain.challengewindow.ChallengeWindow;
import com.paynalty.domain.challengewindow.ChallengeWindowRepository;
import com.paynalty.domain.challengewindow.ChallengeWindowStatus;
import com.paynalty.domain.penalty.Penalty;
import com.paynalty.domain.penalty.PenaltyRepository;
import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeVerificationRepository challengeVerificationRepository;
    private final ChallengeMemberRepository challengeMemberRepository;
    private final ChallengeWindowRepository challengeWindowRepository;
    private final PenaltyRepository penaltyRepository;

    @Bean
    @Profile("!test") // 테스트 환경이 아닐 때만 실행 (선택 사항)
    public CommandLineRunner initData() {
        return args -> {
            if (userRepository.count() == 0) {
                // 1. 홍길동 (ChallengeRequest 예시 데이터)
                userRepository.save(User.builder()
                        .name("홍길동")
                        .phoneNum("010-1111-2222")
                        .email("hong@example.com")
                        .tossId(1001L)
                        .build());
                */
/*
                // 2. 이순신 (ChallengeRequest 예시 데이터)
                userRepository.save(User.builder()
                        .name("이순신")
                        .phoneNum("010-3333-9999")
                        .email("lee@example.com")
                        .tossId(1002L)
                        .build());

                // 3. 나머지 8명의 테스트 유저 생성
                for (int i = 3; i <= 10; i++) {
                    userRepository.save(User.builder()
                            .name("테스트유저" + i)
                            .phoneNum("010-0000-000" + i)
                            .email("test" + i + "@example.com")
                            .tossId(1000L + i)
                            .build());
                }
                            *//*


                System.out.println("테스트용 User 데이터 10개가 생성되었습니다.");
            }
            */
/*
            if (challengeRepository.count() == 0) {

                List<User> users = userRepository.findAll();
                LocalDate today = LocalDate.now();

                // ✅ 챌린지 1: PENDING (시작 전)
                challengeRepository.save(
                        Challenge.builder()
                                .title("건강한 아침 식사하기")
                                .startDate(today.plusDays(3))
                                .endDate(today.plusDays(24)) // 3주
                                .frequency(3)
                                .penaltyAmount(10000L)
                                .status(Challenge.calculateStatus(
                                        today.plusDays(3),
                                        today.plusDays(24)))
                                .verificationType(VerificationType.PHOTO)
                                .verifyStartAt(LocalTime.of(9, 0))
                                .verifyEndAt(LocalTime.of(18, 0))
                                .daysOfWeek(List.of(DayOfWeekType.MON, DayOfWeekType.WED, DayOfWeekType.FRI))
                                .user(users.get(0))
                                .build());

                // ✅ 챌린지 2: ACTIVE (진행 중 - 요일 지정)
                challengeRepository.save(
                        Challenge.builder()
                                .title("주 3회 조깅하고 인증하기")
                                .startDate(today.minusDays(21)) // 3주 전 시작
                                .endDate(today.plusDays(14)) // 2주 후 종료
                                .frequency(3)
                                .penaltyAmount(10000L)
                                .status(Challenge.calculateStatus(
                                        today.minusDays(21),
                                        today.plusDays(14)))
                                .verificationType(VerificationType.PHOTO)
                                .verifyStartAt(LocalTime.of(9, 0))
                                .verifyEndAt(LocalTime.of(18, 0))
                                .daysOfWeek(List.of(DayOfWeekType.MON, DayOfWeekType.WED, DayOfWeekType.FRI))
                                .user(users.get(0))
                                .build());

                // ✅ 챌린지 3: ACTIVE (진행 중 - 저녁 시간대)
                challengeRepository.save(
                        Challenge.builder()
                                .title("하루 한 페이지 일기 쓰기")
                                .startDate(today.minusDays(21)) // 3주 전 시작
                                .endDate(today.plusDays(14)) // 2주 후 종료
                                .frequency(6)
                                .penaltyAmount(10000L)
                                .status(Challenge.calculateStatus(
                                        today.minusDays(21),
                                        today.plusDays(14)))
                                .verificationType(VerificationType.PHOTO)
                                .verifyStartAt(LocalTime.of(18, 0))
                                .verifyEndAt(LocalTime.of(23, 59,59))
                                .daysOfWeek(List.of(DayOfWeekType.MON, DayOfWeekType.WED, DayOfWeekType.FRI))
                                .user(users.get(0))
                                .build());

                // ✅ 챌린지 4: ACTIVE (진행 중 - 매일, 24시간)
                challengeRepository.save(
                        Challenge.builder()
                                .title("매일 물 2L 마시기")
                                .startDate(today.minusDays(21)) // 3주 전 시작
                                .endDate(today.plusDays(14)) // 2주 후 종료
                                .frequency(7) // 주 7회 (매일)
                                .penaltyAmount(10000L)
                                .status(Challenge.calculateStatus(
                                        today.minusDays(21),
                                        today.plusDays(14)))
                                .verificationType(VerificationType.PHOTO)
                                .verifyStartAt(LocalTime.of(0, 0, 0))
                                .verifyEndAt(LocalTime.of(16, 39, 59))
                                .daysOfWeek(List.of(
                                        DayOfWeekType.MON,
                                        DayOfWeekType.TUE,
                                        DayOfWeekType.WED,
                                        DayOfWeekType.THU,
                                        DayOfWeekType.FRI,
                                        DayOfWeekType.SAT,
                                        DayOfWeekType.SUN))
                                .user(users.get(0))
                                .build());

                // ✅ 챌린지 5: COMPLETE (완료됨)
                challengeRepository.save(
                        Challenge.builder()
                                .title("영어 단어 10개 외우기")
                                .startDate(today.minusDays(28))
                                .endDate(today.minusDays(7))
                                .frequency(5)
                                .penaltyAmount(10000L)
                                .status(Challenge.calculateStatus(
                                        today.minusDays(28),
                                        today.minusDays(7)))
                                .verificationType(VerificationType.PHOTO)
                                .verifyStartAt(LocalTime.of(9, 0))
                                .verifyEndAt(LocalTime.of(18, 0))
                                .daysOfWeek(List.of(
                                        DayOfWeekType.MON,
                                        DayOfWeekType.TUE,
                                        DayOfWeekType.WED,
                                        DayOfWeekType.THU,
                                        DayOfWeekType.FRI))
                                .user(users.get(0))
                                .build());

                // ✅ 챌린지 6: ACTIVE (진행 중 - 화/목/토/일, 오전)
                challengeRepository.save(
                        Challenge.builder()
                                .title("스쿼트 50개 하고 인증하기")
                                .startDate(today.minusDays(21)) // 3주 전 시작
                                .endDate(today.plusDays(14)) // 2주 후 종료
                                .frequency(4)
                                .penaltyAmount(10000L)
                                .status(Challenge.calculateStatus(
                                        today.minusDays(21),
                                        today.plusDays(14)))
                                .verificationType(VerificationType.PHOTO)
                                .verifyStartAt(LocalTime.of(9, 0))
                                .verifyEndAt(LocalTime.of(18, 0))
                                .daysOfWeek(List.of(
                                        DayOfWeekType.TUE,
                                        DayOfWeekType.THU,
                                        DayOfWeekType.SAT,
                                        DayOfWeekType.SUN))
                                .user(users.get(0))
                                .build());

                // ✅ 챌린지 7: ACTIVE (진행 중 - 화/목/토/일, 저녁)
                challengeRepository.save(
                        Challenge.builder()
                                .title("책 10페이지 읽고 인증하기")
                                .startDate(today.minusDays(21)) // 3주 전 시작
                                .endDate(today.plusDays(14)) // 2주 후 종료
                                .frequency(4)
                                .penaltyAmount(10000L)
                                .status(Challenge.calculateStatus(
                                        today.minusDays(21),
                                        today.plusDays(14)))
                                .verificationType(VerificationType.PHOTO)
                                .verifyStartAt(LocalTime.of(18, 0))
                                .verifyEndAt(LocalTime.of(23, 59))
                                .daysOfWeek(List.of(
                                        DayOfWeekType.TUE,
                                        DayOfWeekType.THU,
                                        DayOfWeekType.SAT,
                                        DayOfWeekType.SUN))
                                .user(users.get(0))
                                .build());

                // ✅ 챌린지 8: ACTIVE (진행 중 - 주간 횟수 기반, 6회)
                challengeRepository.save(
                        Challenge.builder()
                                .title("주간 횟수 기반 챌린지 테스트 (6회)")
                                .startDate(today.minusDays(21)) // 3주 전 시작
                                .endDate(today.plusDays(14)) // 2주 후 종료
                                .frequency(6) // 주간 6회 인증
                                .penaltyAmount(10000L)
                                .status(Challenge.calculateStatus(
                                        today.minusDays(21),
                                        today.plusDays(14)))
                                .verificationType(VerificationType.PHOTO)
                                .verifyStartAt(LocalTime.of(6, 0)) // 06:00
                                .verifyEndAt(LocalTime.of(18, 9,50)) // 17:00
                                .daysOfWeek(null) // 주간 횟수 기반 (daysOfWeek null)
                                .user(users.get(0))
                                .build());

                System.out.println("✅ 테스트용 Challenge 데이터 생성 완료 (8개)");
            }

            if (challengeMemberRepository.count() == 0) {

                List<User> users = userRepository.findAll();
                List<Challenge> challenges = challengeRepository.findAll();

                MemberRole role = MemberRole.CHALLENGER;

                for (Challenge challenge : challenges) {

                    // 1️⃣ 테스트용 고정 멤버 (userId=1, 2, 3 항상 포함)
                    for (int i = 0; i < 3 && i < users.size(); i++) {
                        if (i == 0) {
                            role = MemberRole.CREATOR;
                        } else {
                            role = MemberRole.CHALLENGER;
                        }

                        challengeMemberRepository.save(
                                ChallengeMember.builder()
                                        .user(users.get(i))
                                        .challenge(challenge)
                                        .isSuccess(challenge.getStatus())
                                        .endAt(challenge.getEndDate())
                                        .role(role)
                                        .build());
                    }

                    // 2️⃣ 추가 참가자 (0~2명 랜덤, userId=4부터)
                    int additionalCount = (int) (Math.random() * 3); // 0, 1, or 2

                    for (int i = 3; i < 3 + additionalCount && i < users.size(); i++) {
                        challengeMemberRepository.save(
                                ChallengeMember.builder()
                                        .user(users.get(i))
                                        .challenge(challenge)
                                        .isSuccess(challenge.getStatus())
                                        .endAt(challenge.getEndDate())
                                        .role(role)
                                        .build());
                    }
                }

                System.out.println("테스트용 ChallengeMember 데이터가 생성되었습니다.");
                System.out.println("✅ userId=1, 2, 3은 모든 챌린지에 포함됨");
            }

            if (challengeVerificationRepository.count() == 0) {

                List<User> users = userRepository.findAll();
                List<Challenge> challenges = challengeRepository.findAll();
                LocalDate today = LocalDate.now();
                LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1); // 이번 주 월요일

                // ✅ 챌린지 2번 인증 데이터 (ACTIVE - 오전)
                Challenge activeChallenge1 = challenges.get(1);

                // 과거 인증 데이터 (시작일부터 어제까지)
                for (LocalDate date = activeChallenge1.getStartDate(); date.isBefore(today); date = date.plusDays(1)) {
                    if (date.getDayOfWeek().getValue() == 1 ||
                            date.getDayOfWeek().getValue() == 3 ||
                            date.getDayOfWeek().getValue() == 5) {

                        for (int i = 0; i < 3 && i < users.size(); i++) {
                            challengeVerificationRepository.save(
                                    ChallengeVerification.builder()
                                            .date(date)
                                            .user(users.get(i))
                                            .challenge(activeChallenge1)
                                            .imageUrl(
                                                    "https://us.123rf.com/450wm/martialred/martialred1507/martialred150700661/42613290-landscape-photo-image-flat-icon-for-apps-and-websites.jpg")
                                            .build());
                        }
                    }
                }

                // 오늘: userId=2만 인증
                if (today.getDayOfWeek().getValue() == 1 ||
                        today.getDayOfWeek().getValue() == 3 ||
                        today.getDayOfWeek().getValue() == 5) {

                    challengeVerificationRepository.save(
                            ChallengeVerification.builder()
                                    .date(today)
                                    .user(users.get(1))
                                    .challenge(activeChallenge1)
                                    .imageUrl(
                                            "https://us.123rf.com/450wm/martialred/martialred1507/martialred150700661/42613290-landscape-photo-image-flat-icon-for-apps-and-websites.jpg")
                                    .build());

                    // 오늘: userId=1도 인증 추가
                    challengeVerificationRepository.save(
                            ChallengeVerification.builder()
                                    .date(today)
                                    .user(users.get(0))
                                    .challenge(activeChallenge1)
                                    .imageUrl(
                                            "https://us.123rf.com/450wm/martialred/martialred1507/martialred150700661/42613290-landscape-photo-image-flat-icon-for-apps-and-websites.jpg")
                                    .build());
                }

                // ✅ 챌린지 3번 인증 데이터 (ACTIVE - 저녁)
                Challenge activeChallenge2 = challenges.get(2);

                // 과거 인증 데이터 (시작일부터 어제까지)
                for (LocalDate date = activeChallenge2.getStartDate(); date.isBefore(today); date = date.plusDays(1)) {
                    if (date.getDayOfWeek().getValue() == 1 ||
                            date.getDayOfWeek().getValue() == 3 ||
                            date.getDayOfWeek().getValue() == 5) {

                        for (int i = 0; i < 3 && i < users.size(); i++) {
                            challengeVerificationRepository.save(
                                    ChallengeVerification.builder()
                                            .date(date)
                                            .user(users.get(i))
                                            .challenge(activeChallenge2)
                                            .imageUrl(
                                                    "https://us.123rf.com/450wm/martialred/martialred1507/martialred150700661/42613290-landscape-photo-image-flat-icon-for-apps-and-websites.jpg")
                                            .build());
                        }
                    }
                }

                // 오늘: userId=3만 인증 (저녁 시간대는 userId=1,2가 인증 안 함)
                if (today.getDayOfWeek().getValue() == 1 ||
                        today.getDayOfWeek().getValue() == 3 ||
                        today.getDayOfWeek().getValue() == 5) {

                    challengeVerificationRepository.save(
                            ChallengeVerification.builder()
                                    .date(today)
                                    .user(users.get(2))
                                    .challenge(activeChallenge2)
                                    .imageUrl(
                                            "https://us.123rf.com/450wm/martialred/martialred1507/martialred150700661/42613290-landscape-photo-image-flat-icon-for-apps-and-websites.jpg")
                                    .build());
                }

                // ✅ 챌린지 4번 인증 데이터 (ACTIVE - 매일)
                Challenge activeChallengeDaily = challenges.get(3);

                // 과거 인증 데이터 (시작일부터 어제까지 매일)
                for (LocalDate date = activeChallengeDaily.getStartDate(); date
                        .isBefore(today); date = date.plusDays(1)) {
                    for (int i = 0; i < 3 && i < users.size(); i++) {
                        challengeVerificationRepository.save(
                                ChallengeVerification.builder()
                                        .date(date)
                                        .user(users.get(i))
                                        .challenge(activeChallengeDaily)
                                        .imageUrl(
                                                "https://us.123rf.com/450wm/martialred/martialred1507/martialred150700661/42613290-landscape-photo-image-flat-icon-for-apps-and-websites.jpg")
                                        .build());
                    }
                }

                // 오늘: userId=2만 인증 (userId=1은 테스트용으로 인증 안 함)
                challengeVerificationRepository.save(
                        ChallengeVerification.builder()
                                .date(today)
                                .user(users.get(1))
                                .challenge(activeChallengeDaily)
                                .imageUrl(
                                        "https://us.123rf.com/450wm/martialred/martialred1507/martialred150700661/42613290-landscape-photo-image-flat-icon-for-apps-and-websites.jpg")
                                .build());

                // userId=1의 오늘 인증은 생성하지 않음 (테스트용 - 사용자가 직접 인증 API 호출)

                // ✅ 챌린지 5번 인증 데이터 (COMPLETE)
                Challenge completeChallenge = challenges.get(4);

                for (LocalDate date = completeChallenge.getStartDate(); !date
                        .isAfter(completeChallenge.getEndDate()); date = date.plusDays(1)) {

                    if (date.getDayOfWeek().getValue() >= 1 && date.getDayOfWeek().getValue() <= 5) {
                        for (int i = 0; i < 3 && i < users.size(); i++) {
                            challengeVerificationRepository.save(
                                    ChallengeVerification.builder()
                                            .date(date)
                                            .user(users.get(i))
                                            .challenge(completeChallenge)
                                            .imageUrl(
                                                    "https://us.123rf.com/450wm/martialred/martialred1507/martialred150700661/42613290-landscape-photo-image-flat-icon-for-apps-and-websites.jpg")
                                            .build());
                        }
                    }
                }

                // ✅ 챌린지 6번 인증 데이터 (ACTIVE - 화/목/토/일, 오전)
                Challenge activeChallenge6 = challenges.get(5);

                // 과거 인증 데이터 (시작일부터 어제까지)
                for (LocalDate date = activeChallenge6.getStartDate(); date.isBefore(today); date = date.plusDays(1)) {
                    if (date.getDayOfWeek().getValue() == 2 ||
                            date.getDayOfWeek().getValue() == 4 ||
                            date.getDayOfWeek().getValue() == 6 ||
                            date.getDayOfWeek().getValue() == 7) {

                        for (int i = 0; i < 3 && i < users.size(); i++) {
                            challengeVerificationRepository.save(
                                    ChallengeVerification.builder()
                                            .date(date)
                                            .user(users.get(i))
                                            .challenge(activeChallenge6)
                                            .imageUrl(
                                                    "https://us.123rf.com/450wm/martialred/martialred1507/martialred150700661/42613290-landscape-photo-image-flat-icon-for-apps-and-websites.jpg")
                                            .build());
                        }
                    }
                }

                // 오늘: userId=1만 인증
                if (today.getDayOfWeek().getValue() == 2 ||
                        today.getDayOfWeek().getValue() == 4 ||
                        today.getDayOfWeek().getValue() == 6 ||
                        today.getDayOfWeek().getValue() == 7) {

                    challengeVerificationRepository.save(
                            ChallengeVerification.builder()
                                    .date(today)
                                    .user(users.get(0))
                                    .challenge(activeChallenge6)
                                    .imageUrl(
                                            "https://us.123rf.com/450wm/martialred/martialred1507/martialred150700661/42613290-landscape-photo-image-flat-icon-for-apps-and-websites.jpg")
                                    .build());
                }

                // ✅ 챌린지 7번 인증 데이터 (ACTIVE - 화/목/토/일, 저녁)
                Challenge activeChallenge7 = challenges.get(6);

                // 과거 인증 데이터 (시작일부터 어제까지)
                for (LocalDate date = activeChallenge7.getStartDate(); date.isBefore(today); date = date.plusDays(1)) {
                    if (date.getDayOfWeek().getValue() == 2 ||
                            date.getDayOfWeek().getValue() == 4 ||
                            date.getDayOfWeek().getValue() == 6 ||
                            date.getDayOfWeek().getValue() == 7) {

                        for (int i = 0; i < 3 && i < users.size(); i++) {
                            challengeVerificationRepository.save(
                                    ChallengeVerification.builder()
                                            .date(date)
                                            .user(users.get(i))
                                            .challenge(activeChallenge7)
                                            .imageUrl(
                                                    "https://previews.123rf.com/images/martialred/martialred1507/martialred150700661/42613290-landscape-photo-image-flat-icon-for-apps-and-websites.avif")
                                            .build());
                        }
                    }
                }

                // 오늘: 모두 인증 안 함 (userId=1,2,3 모두 테스트 가능)
                // (저녁 챌린지는 별도 테스트용)

                System.out.println("✅ 테스트용 인증 데이터 생성 완료");
                System.out.println("   📌 챌린지 2 (09:00~18:00, 월/수/금): userId=2 인증완료");
                System.out.println("   📌 챌린지 3 (18:00~23:59, 월/수/금): userId=3 인증완료");
                System.out.println("   📌 챌린지 4 (00:00~23:59, 매일): userId=2 인증완료");
                System.out.println("   📌 챌린지 6 (09:00~18:00, 화/목/토/일): userId=1 인증완료");
                System.out.println("   📌 챌린지 7 (18:00~23:59, 화/목/토/일): 모두 인증안함");
            }

            // ✅ 챌린지 4번 테스트용 더미 데이터 (userId=1, tossId=1001)
            if (challengeRepository.count() >= 4) {
                Challenge challenge4 = challengeRepository.findById(4L).orElse(null);
                User user1 = userRepository.findByTossId(1001L).orElse(null);

                if (challenge4 != null && user1 != null) {
                    ChallengeMember member1 = challengeMemberRepository
                            .findByChallengeIdAndUserTossIdWithFetch(4L, 1001L)
                            .orElse(null);

                    if (member1 != null) {
                        LocalDate today = LocalDate.now();

                        // 챌린지의 verifyStartAt과 verifyEndAt을 기준으로 Window 생성
                        // (ChallengeWindowGenerator와 동일한 방식)
                        LocalTime verifyStartAt = challenge4.getVerifyStartAt();
                        LocalTime verifyEndAt = challenge4.getVerifyEndAt();

                        // Window 1: 오늘 날짜, 챌린지의 verifyEndAt을 마감 시간으로 사용
                        LocalDateTime window1Start = today.atTime(verifyStartAt);
                        LocalDateTime window1End = today.atTime(verifyEndAt);

                        ChallengeWindow window1 = ChallengeWindow.builder()
                                .challengeId(4L)
                                .tossId(1001L)
                                .challengeWindowStart(window1Start)
                                .challengeWindowEnd(window1End)
                                .challengeWindowStatus(ChallengeWindowStatus.PENDING)
                                .build();
                        challengeWindowRepository.save(window1);

                        // Window 2: FAIL 케이스 - 인증이 없는 경우 (어제 날짜, 마감 시간 지남)
                        LocalDateTime window2Start = today.minusDays(1).atTime(verifyStartAt);
                        LocalDateTime window2End = today.minusDays(1).atTime(verifyEndAt);
                        ChallengeWindow window2 = ChallengeWindow.builder()
                                .challengeId(4L)
                                .tossId(1001L)
                                .challengeWindowStart(window2Start)
                                .challengeWindowEnd(window2End)
                                .challengeWindowStatus(ChallengeWindowStatus.PENDING)
                                .build();
                        challengeWindowRepository.save(window2);

                        // 오늘 날짜에 대한 인증 데이터는 생성하지 않음
                        // 사용자가 직접 인증 API를 호출하여 인증을 생성할 수 있음
                        // - 인증을 하면 → 마감 시간 후 SUCCESS
                        // - 인증을 안 하면 → 마감 시간 후 FAIL

                        // Penalty 데이터 생성 (챌린지 4번, userId=1)
                        // 벌금 1: 3일 전
                        Penalty penalty1 = Penalty.builder()
                                .challengeMember(member1)
                                .penaltyAmount(challenge4.getPenaltyAmount())
                                .build();
                        Penalty savedPenalty1 = penaltyRepository.save(penalty1);
                        savedPenalty1.setCreatedAt(today.minusDays(3).atTime(12, 0));
                        penaltyRepository.save(savedPenalty1);

                        // 벌금 2: 5일 전
                        Penalty penalty2 = Penalty.builder()
                                .challengeMember(member1)
                                .penaltyAmount(challenge4.getPenaltyAmount())
                                .build();
                        Penalty savedPenalty2 = penaltyRepository.save(penalty2);
                        savedPenalty2.setCreatedAt(today.minusDays(5).atTime(15, 30));
                        penaltyRepository.save(savedPenalty2);

                        // 벌금 3: 7일 전
                        Penalty penalty3 = Penalty.builder()
                                .challengeMember(member1)
                                .penaltyAmount(challenge4.getPenaltyAmount())
                                .build();
                        Penalty savedPenalty3 = penaltyRepository.save(penalty3);
                        savedPenalty3.setCreatedAt(today.minusDays(7).atTime(9, 0));
                        penaltyRepository.save(savedPenalty3);

                        // 다른 사용자들의 벌금 데이터 생성 (전체 벌금 조회 테스트용)
                        // userId=2 (tossId=1002)
                        ChallengeMember member2 = challengeMemberRepository
                                .findByChallengeIdAndUserTossIdWithFetch(4L, 1002L)
                                .orElse(null);

                        if (member2 != null) {
                            // userId=2 벌금 1: 4일 전
                            Penalty penalty2_1 = Penalty.builder()
                                    .challengeMember(member2)
                                    .penaltyAmount(challenge4.getPenaltyAmount())
                                    .build();
                            Penalty savedPenalty2_1 = penaltyRepository.save(penalty2_1);
                            savedPenalty2_1.setCreatedAt(today.minusDays(4).atTime(11, 0));
                            penaltyRepository.save(savedPenalty2_1);

                            // userId=2 벌금 2: 6일 전
                            Penalty penalty2_2 = Penalty.builder()
                                    .challengeMember(member2)
                                    .penaltyAmount(challenge4.getPenaltyAmount())
                                    .build();
                            Penalty savedPenalty2_2 = penaltyRepository.save(penalty2_2);
                            savedPenalty2_2.setCreatedAt(today.minusDays(6).atTime(14, 0));
                            penaltyRepository.save(savedPenalty2_2);
                        }

                        // userId=3 (tossId=1003)
                        ChallengeMember member3 = challengeMemberRepository
                                .findByChallengeIdAndUserTossIdWithFetch(4L, 1003L)
                                .orElse(null);

                        if (member3 != null) {
                            // userId=3 벌금 1: 2일 전
                            Penalty penalty3_1 = Penalty.builder()
                                    .challengeMember(member3)
                                    .penaltyAmount(challenge4.getPenaltyAmount())
                                    .build();
                            Penalty savedPenalty3_1 = penaltyRepository.save(penalty3_1);
                            savedPenalty3_1.setCreatedAt(today.minusDays(2).atTime(16, 0));
                            penaltyRepository.save(savedPenalty3_1);
                        }

                        System.out.println("✅ 챌린지 4번 테스트용 더미 데이터 생성 완료");
                        System.out.println("   📌 ChallengeWindow: 2개 (PENDING, userId=1)");
                        System.out.println("      - Window 1: 오늘, 인증 없음, 마감 시간: " + window1End + " (챌린지 verifyEndAt 기준)");
                        System.out.println("         → 인증을 하면 SUCCESS, 안 하면 FAIL");
                        System.out.println("      - Window 2: 어제, 인증 없음, 마감 시간 지남 (FAIL 예상)");
                        System.out.println("   📌 ChallengeVerification: 오늘 인증 없음 (사용자가 직접 생성)");
                        System.out.println("   📌 Penalty: 총 6개");
                        System.out.println("      - userId=1: 3개 (3일 전, 5일 전, 7일 전)");
                        System.out.println("      - userId=2: 2개 (4일 전, 6일 전)");
                        System.out.println("      - userId=3: 1개 (2일 전)");
                        System.out.println("   ⏰ 스케줄러는 10분마다 실행되며, " + window1End + " 이후 Window 1이 처리됩니다.");
                        System.out.println("   📝 테스트 방법:");
                        System.out.println("      1. 챌린지 4번의 verifyEndAt을 원하는 시간으로 수정 후 빌드");
                        System.out.println("      2. 마감 시간 전에 인증 API 호출 → 인증 생성");
                        System.out.println("      3. 마감 시간 후 스케줄러 실행 → SUCCESS 확인");
                        System.out.println("      4. 또는 인증 없이 마감 시간 후 → FAIL 확인");
                    }
                }
            }

            // ✅ 챌린지 8번 테스트용 더미 데이터 (주간 횟수 기반, 6회)
            if (challengeRepository.count() >= 8) {
                Challenge challenge8 = challengeRepository.findById(8L).orElse(null);
                User user1 = userRepository.findByTossId(1001L).orElse(null);

                if (challenge8 != null && user1 != null && challenge8.getDaysOfWeek() == null) {
                    ChallengeMember member1 = challengeMemberRepository
                            .findByChallengeIdAndUserTossIdWithFetch(8L, 1001L)
                            .orElse(null);

                    if (member1 != null) {
                        LocalDate today = LocalDate.now();
                        LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
                        LocalDate weekEnd = weekStart.plusDays(6); // 일요일

                        LocalTime verifyStartAt = challenge8.getVerifyStartAt() != null
                                ? challenge8.getVerifyStartAt()
                                : LocalTime.of(6, 0);
                        LocalTime verifyEndAt = challenge8.getVerifyEndAt() != null
                                ? challenge8.getVerifyEndAt()
                                : LocalTime.of(17, 0);

                        // 이번 주 ChallengeWindow 7개 생성
                        // 벌금이 1회 생성되었다는 것은 화요일 Window가 이미 FAIL 상태여야 함
                        for (int i = 0; i < 7; i++) {
                            LocalDate windowDate = weekStart.plusDays(i);

                            // 이미 존재하는지 확인
                            boolean exists = challengeWindowRepository
                                    .findByChallengeIdInAndTossIdInAndChallengeWindowStartBetween(
                                            java.util.Set.of(8L),
                                            java.util.Set.of(1001L),
                                            windowDate.atTime(verifyStartAt),
                                            windowDate.atTime(verifyStartAt).plusDays(1)
                                    ).stream()
                                    .anyMatch(w -> w.getChallengeWindowStart().toLocalDate().equals(windowDate));

                            if (!exists) {
                                // 화요일(i=1)은 벌금이 이미 생성되었으므로 FAIL 상태
                                // 월요일(i=0)은 아직 마감 시간이 지나지 않았거나 벌금이 발생하지 않았으므로 PENDING
                                // 수요일 이후(i>=2)는 아직 마감 시간이 지나지 않았으므로 PENDING
                                ChallengeWindowStatus status = (i == 1)
                                        ? ChallengeWindowStatus.FAIL
                                        : ChallengeWindowStatus.PENDING;

                                ChallengeWindow window = ChallengeWindow.builder()
                                        .challengeId(8L)
                                        .tossId(1001L)
                                        .challengeWindowStart(windowDate.atTime(verifyStartAt))
                                        .challengeWindowEnd(windowDate.atTime(verifyEndAt))
                                        .challengeWindowStatus(status)
                                        .build();
                                challengeWindowRepository.save(window);
                            }
                        }

                        // 이미 생성된 벌금 1회 (현재 인증 0회, 남은 인증 6회 > 남은 일수 5일)
                        // 이번 주에 이미 생성된 벌금이 있는지 확인
                        Long existingPenaltyCount = penaltyRepository.countWeeklyPenalties(
                                8L, 1001L, weekStart, weekEnd);

                        if (existingPenaltyCount == 0) {
                            Penalty penalty = Penalty.builder()
                                    .challengeMember(member1)
                                    .penaltyAmount(challenge8.getPenaltyAmount())
                                    .build();
                            Penalty savedPenalty = penaltyRepository.save(penalty);
                            // 이번 주 범위 내로 createdAt 설정 (예: 월요일 오전)
                            savedPenalty.setCreatedAt(weekStart.atTime(10, 0));
                            penaltyRepository.save(savedPenalty);
                        }

                        System.out.println("✅ 챌린지 8번 테스트용 더미 데이터 생성 완료 (주간 횟수 기반)");
                        System.out.println("   📌 주간 인증 횟수: 6회");
                        System.out.println("   📌 현재 주간 인증: 0회");
                        System.out.println("   📌 오늘(수요일) 인증: 없음 (테스트용)");
                        System.out.println("   📌 ChallengeWindow: 이번 주 7개");
                        System.out.println("      - 월요일: PENDING (아직 마감 시간 전 또는 벌금 없음)");
                        System.out.println("      - 화요일: FAIL (벌금 1회 생성됨)");
                        System.out.println("      - 수요일~일요일: PENDING (아직 마감 시간 전)");
                        System.out.println("   📌 이미 생성된 벌금: 1회 (화요일 Window 처리 시 생성)");
                        System.out.println("   ⏰ 테스트 시나리오:");
                        System.out.println("      - 오늘(수요일) 마감 시간(17:00)까지 인증 안 하면 → 벌금 추가 발생, 수요일 Window FAIL");
                        System.out.println("      - 오늘(수요일) 인증하면 → 남은 인증 5회, 남은 일수 4일 → 벌금 발생 안 함");
                    }
                }
            }
            *//*

        };
    }
}

*/
