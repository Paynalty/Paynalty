package com.paynalty.global.config;

import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;

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
        };
    }
}
