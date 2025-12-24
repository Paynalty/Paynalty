package com.paynalty.global.config;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.challenge.DayOfWeekType;
import com.paynalty.domain.challenge.VerificationType;
import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengemember.ChallengeMemberRepository;
import com.paynalty.domain.challengeverification.ChallengeVerification;
import com.paynalty.domain.challengeverification.ChallengeVerificationRepository;
import com.paynalty.domain.challengeverification.VerificationStatus;
import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
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

                // 1️⃣ 진행 중 - 평일 저녁 운동
                challengeRepository.save(
                        Challenge.builder()
                                .title("평일 저녁 운동 챌린지")
                                .startDate(LocalDate.now().minusDays(5))
                                .endDate(LocalDate.now().plusDays(20))
                                .frequency(3)
                                .penaltyAmount(10000L)
                                .status(Challenge.calculateStatus(
                                        LocalDate.now().minusDays(5),
                                        LocalDate.now().plusDays(20)
                                ))
                                .verificationType(VerificationType.PHOTO)
                                .verifyStartAt(LocalTime.of(0, 0))
                                .verifyEndAt(LocalTime.of(23, 59,59))
                                .daysOfWeek(List.of(
                                        DayOfWeekType.MON,
                                        DayOfWeekType.WED,
                                        DayOfWeekType.FRI
                                ))
                                .user(users.get(0)) // 홍길동
                                .build()
                );

                // 2️⃣ 진행 중 - 출근 인증
                challengeRepository.save(
                        Challenge.builder()
                                .title("출근 인증 챌린지")
                                .startDate(LocalDate.now().minusDays(2))
                                .endDate(LocalDate.now().plusDays(30))
                                .frequency(5)
                                .penaltyAmount(5000L)
                                .status(Challenge.calculateStatus(
                                        LocalDate.now().minusDays(2),
                                        LocalDate.now().plusDays(30)
                                ))
                                .verificationType(VerificationType.PHOTO)
                                .verifyStartAt(LocalTime.of(0, 0))
                                .verifyEndAt(LocalTime.of(23, 59,59))
                                .daysOfWeek(List.of(
                                        DayOfWeekType.MON,
                                        DayOfWeekType.TUE,
                                        DayOfWeekType.WED,
                                        DayOfWeekType.THU,
                                        DayOfWeekType.FRI
                                ))
                                .user(users.get(0)) // 이순신
                                .build()
                );

                // 3️⃣ 시작 전 - 주말 독서
                challengeRepository.save(
                        Challenge.builder()
                                .title("주말 독서 챌린지")
                                .startDate(LocalDate.now().plusDays(3))
                                .endDate(LocalDate.now().plusDays(40))
                                .frequency(2)
                                .penaltyAmount(7000L)
                                .status(Challenge.calculateStatus(
                                        LocalDate.now().plusDays(3),
                                        LocalDate.now().plusDays(40)
                                ))
                                .verificationType(VerificationType.TEXT)
                                .verifyStartAt(LocalTime.of(0, 0))
                                .verifyEndAt(LocalTime.of(23, 59,59))
                                .daysOfWeek(List.of(
                                        DayOfWeekType.SAT,
                                        DayOfWeekType.SUN
                                ))
                                .user(users.get(0)) // 테스트유저3
                                .build()
                );

                // 4️⃣ 완료됨 - 과거 챌린지
                challengeRepository.save(
                        Challenge.builder()
                                .title("완료된 미라클 모닝")
                                .startDate(LocalDate.now().minusDays(30))
                                .endDate(LocalDate.now().minusDays(1))
                                .frequency(2)
                                .penaltyAmount(3000L)
                                .status(Challenge.calculateStatus(
                                        LocalDate.now().minusDays(30),
                                        LocalDate.now().minusDays(1)
                                ))
                                .verificationType(VerificationType.PHOTO)
                                .verifyStartAt(LocalTime.of(0, 0))
                                .verifyEndAt(LocalTime.of(23, 59,59))
                                .daysOfWeek(List.of(
                                        DayOfWeekType.TUE,
                                        DayOfWeekType.THU
                                ))
                                .user(users.get(0)) // 테스트유저4
                                .build()
                );
            }

            if (challengeMemberRepository.count() == 0) {

                List<User> users = userRepository.findAll();
                List<Challenge> challenges = challengeRepository.findAll();

                for (Challenge challenge : challenges) {

                    // 1️⃣ 방장 (항상 포함)
                    challengeMemberRepository.save(
                            ChallengeMember.builder()
                                    .user(users.get(0)) // 방장 고정
                                    .challenge(challenge)
                                    .isSuccess(challenge.getStatus())
                                    .endAt(challenge.getEndDate())
                                    .build()
                    );

// 2️⃣ 기본 참가자 (항상 포함)
                    if (users.size() > 1) {
                        challengeMemberRepository.save(
                                ChallengeMember.builder()
                                        .user(users.get(1)) // 고정 참가자
                                        .challenge(challenge)
                                        .isSuccess(challenge.getStatus())
                                        .endAt(challenge.getEndDate())
                                        .build()
                        );
                    }

// 3️⃣ 추가 참가자 수 (0~2명 정도)
                    int additionalCount = (int) (Math.random() * 3); // 0~2

                    for (int i = 2; i < 2 + additionalCount && i < users.size(); i++) {
                        challengeMemberRepository.save(
                                ChallengeMember.builder()
                                        .user(users.get(i)) // users.get(2), (3) ...
                                        .challenge(challenge)
                                        .isSuccess(challenge.getStatus())
                                        .endAt(challenge.getEndDate())
                                        .build()
                        );
                    }
                }
            }

            if (challengeVerificationRepository.count() == 0){

            List<User> users = userRepository.findAll();
            List<Challenge> challenges = challengeRepository.findAll();
            // userid =1
                challengeVerificationRepository.save(
                    ChallengeVerification.builder()
                            .date(LocalDate.of(2025,12,19))
                            .user(users.getFirst())
                            .challenge(challenges.getFirst())
                            .imageUrl("https://example.com/image.jpg")
                            .status(VerificationStatus.FAIL)
                            .build()
                );
                challengeVerificationRepository.save(
                        ChallengeVerification.builder()
                                .date(LocalDate.of(2025,12,22))
                                .user(users.getFirst())
                                .challenge(challenges.getFirst())
                                .imageUrl("https://example.com/image.jpg")
                                .status(VerificationStatus.SUCCESS)
                                .build()
                );
                challengeVerificationRepository.save(
                        ChallengeVerification.builder()
                                .date(LocalDate.of(2025,12,24))
                                .user(users.getFirst())
                                .challenge(challenges.getFirst())
                                .imageUrl("https://example.com/image.jpg")
                                .status(VerificationStatus.SUCCESS)
                                .build()
                );

                //userId = 2;
                challengeVerificationRepository.save(
                        ChallengeVerification.builder()
                                .date(LocalDate.of(2025,12,19))
                                .user(users.get(1))
                                .challenge(challenges.getFirst())
                                .imageUrl("https://example.com/image.jpg")
                                .status(VerificationStatus.SUCCESS)
                                .build()
                );
                challengeVerificationRepository.save(
                        ChallengeVerification.builder()
                                .date(LocalDate.of(2025,12,22))
                                .user(users.get(1))
                                .challenge(challenges.getFirst())
                                .imageUrl("https://example.com/image.jpg")
                                .status(VerificationStatus.SUCCESS)
                                .build()
                );

                // challengeId =2
                // userid =1
                challengeVerificationRepository.save(
                        ChallengeVerification.builder()
                                .date(LocalDate.of(2025,12,22))
                                .user(users.getFirst())
                                .challenge(challenges.get(1))
                                .imageUrl("https://example.com/image.jpg")
                                .status(VerificationStatus.FAIL)
                                .build()
                );
                challengeVerificationRepository.save(
                        ChallengeVerification.builder()
                                .date(LocalDate.of(2025,12,23))
                                .user(users.getFirst())
                                .challenge(challenges.get(1))
                                .imageUrl("https://example.com/image.jpg")
                                .status(VerificationStatus.SUCCESS)
                                .build()
                );
                challengeVerificationRepository.save(
                        ChallengeVerification.builder()
                                .date(LocalDate.of(2025,12,24))
                                .user(users.getFirst())
                                .challenge(challenges.get(1))
                                .imageUrl("https://example.com/image.jpg")
                                .status(VerificationStatus.SUCCESS)
                                .build()
                );

                //userId = 2;
                challengeVerificationRepository.save(
                        ChallengeVerification.builder()
                                .date(LocalDate.of(2025,12,22))
                                .user(users.get(1))
                                .challenge(challenges.get(1))
                                .imageUrl("https://example.com/image.jpg")
                                .status(VerificationStatus.SUCCESS)
                                .build()
                );
                challengeVerificationRepository.save(
                        ChallengeVerification.builder()
                                .date(LocalDate.of(2025,12,24))
                                .user(users.get(1))
                                .challenge(challenges.get(1))
                                .imageUrl("https://example.com/image.jpg")
                                .status(VerificationStatus.SUCCESS)
                                .build()
                );



            }



        };
    }
}
