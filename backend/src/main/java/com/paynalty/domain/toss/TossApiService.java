package com.paynalty.domain.toss;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


/**
 * 토스 앱인토스 연동 서비스
 *
 * - 프론트:  토스 UI(토스페이, 인앱 결제)에서 결제/송금 플로우 처리
 * - 백엔드: 로그인 또는 토큰 교환, 결제 결과 검증, 벌금(Penalty) 기록
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TossApiService {

    @Qualifier("tossRestTemplate")
    private final RestTemplate tossRestTemplate;

    @Value("${toss.api.base-url}")
    private String tossApiBaseUrl;

    /**
     * 토스 로그인 (사용자 인증)
     *
     * - 토스에서 authorizationCode를 받은 뒤,
     *   이 메서드를 호출해 accessToken 등을 교환합니다.
     *
     * @param authorizationCode 토스에서 받은 인증 코드
     * @return 액세스 토큰 및 사용자 정보
     */
    public TossLoginResponse login(String authorizationCode) {
        try {
            log.info("토스 로그인 요청: authorizationCode={}", authorizationCode);

            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청 body 생성
            Map<String, String> requestBody = Map.of(
                    "code", authorizationCode,
                    "grant_type", "authorization_code"
            );

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            // 토스 API 호출 (mTLS 자동 적용됨)
            String url = tossApiBaseUrl + "/oauth/token";
            ResponseEntity<TossLoginResponse> response = tossRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    TossLoginResponse.class
            );

            log.info("토스 로그인 성공: userId={}", response.getBody().getUserId());
            return response.getBody();

        } catch (Exception e) {
            log.error("토스 로그인 실패", e);
            throw new RuntimeException("토스 로그인 실패: " + e.getMessage(), e);
        }
    }
}

