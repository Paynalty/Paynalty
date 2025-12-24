package com.paynalty.global.config;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.challenge.DayOfWeekType;
import com.paynalty.domain.challenge.VerificationType;
import com.paynalty.domain.challengeverification.ChallengeVerificationRepository;
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
                                .verifyStartAt(LocalTime.of(18, 0))
                                .verifyEndAt(LocalTime.of(23, 0))
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
                                .verifyStartAt(LocalTime.of(6, 0))
                                .verifyEndAt(LocalTime.of(9, 0))
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
                                .verifyStartAt(LocalTime.of(10, 0))
                                .verifyEndAt(LocalTime.of(22, 0))
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
                                .verifyStartAt(LocalTime.of(5, 0))
                                .verifyEndAt(LocalTime.of(8, 0))
                                .daysOfWeek(List.of(
                                        DayOfWeekType.TUE,
                                        DayOfWeekType.THU
                                ))
                                .user(users.get(0)) // 테스트유저4
                                .build()
                );
            }

        };
    }
}
