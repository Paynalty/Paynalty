package com.paynalty.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.paynalty.global.security.CustomUserDetailsService;
import com.paynalty.global.security.jwt.JwtAccessDeniedHandler;
import com.paynalty.global.security.jwt.JwtAuthenticationEntryPoint;
import com.paynalty.global.security.jwt.JwtAuthenticationFilter;
import com.paynalty.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtProvider jwtProvider;
        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
        private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
        private final CustomUserDetailsService customUserDetailsService;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // CSRF 비활성화 (JWT 사용 시)
                                .csrf(AbstractHttpConfigurer::disable)

                                // JWT 예외 처리
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                                .accessDeniedHandler(jwtAccessDeniedHandler))

                                // CORS 설정
                                .cors(cors -> cors.configure(http))

                                // 세션 사용 안 함 (JWT 사용)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // 요청 권한 설정
                                .authorizeHttpRequests(auth -> auth
                                                // 인증 없이 접근 가능한 경로
                                                .requestMatchers(
                                                                "/toss/**",
                                                                "/api/auth/**", // 인증 관련
                                                                "/uploads/verifications/**", // 업로드된 인증 이미지
                                                                "/h2-console/**", // H2 콘솔 (개발 환경)
                                                                "/swagger-ui/**", // Swagger UI
                                                                "/api-docs/**", // API 문서
                                                                "/swagger-ui.html", // Swagger UI (Spring Boot 2.x)
                                                                "/swagger-ui/index.html", // Swagger UI (Spring Boot
                                                                                          // 3.x)
                                                                "/v3/api-docs/**" // OpenAPI 3.0 문서
                                                ).permitAll()

                                                // 그 외 모든 요청은 인증 필요
                                                .anyRequest().authenticated())

                                // JwtAuthenticationFilter 추가
                                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, customUserDetailsService),
                                                UsernamePasswordAuthenticationFilter.class);

                // H2 Console을 위한 설정 (개발 환경)
                http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}