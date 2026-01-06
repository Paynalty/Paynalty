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

                System.out.println("테스트용 User 데이터 10개가 생성되었습니다.");
            }
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
                                .frequency(3)
                                .penaltyAmount(10000L)
                                .status(Challenge.calculateStatus(
                                        today.minusDays(21),
                                        today.plusDays(14)))
                                .verificationType(VerificationType.PHOTO)
                                .verifyStartAt(LocalTime.of(18, 0))
                                .verifyEndAt(LocalTime.of(23, 59))
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
                                .verifyEndAt(LocalTime.of(23, 59, 59))
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

                System.out.println("✅ 테스트용 Challenge 데이터 생성 완료 (7개)");
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
                                            .status(VerificationStatus.VALID)
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
                                    .status(VerificationStatus.VALID)
                                    .build());

                    // 오늘: userId=1도 인증 추가
                    challengeVerificationRepository.save(
                            ChallengeVerification.builder()
                                    .date(today)
                                    .user(users.get(0))
                                    .challenge(activeChallenge1)
                                    .imageUrl(
                                            "https://us.123rf.com/450wm/martialred/martialred1507/martialred150700661/42613290-landscape-photo-image-flat-icon-for-apps-and-websites.jpg")
                                    .status(VerificationStatus.VALID)
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
                                            .status(VerificationStatus.VALID)
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
                                    .status(VerificationStatus.VALID)
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
                                        .status(VerificationStatus.VALID)
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
                                .status(VerificationStatus.VALID)
                                .build());

                // 오늘: userId=1도 인증 추가 (매일 물마시기)
                challengeVerificationRepository.save(
                        ChallengeVerification.builder()
                                .date(today)
                                .user(users.get(0))
                                .challenge(activeChallengeDaily)
                                .imageUrl(
                                        "https://us.123rf.com/450wm/martialred/martialred1507/martialred150700661/42613290-landscape-photo-image-flat-icon-for-apps-and-websites.jpg")
                                .status(VerificationStatus.VALID)
                                .build());

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
                                            .status(VerificationStatus.VALID)
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
                                            .status(VerificationStatus.VALID)
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
                                    .status(VerificationStatus.VALID)
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
                                            .status(VerificationStatus.VALID)
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

        };
    }
}
