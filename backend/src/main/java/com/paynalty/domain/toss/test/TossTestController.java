package com.paynalty.domain.toss.test;

import com.paynalty.domain.toss.TossApiService;
import com.paynalty.domain.toss.TossLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 토스 mTLS 및 로그인 연동 테스트용 컨트롤러
 *
 * 실제 운영 플로우 전에,
 * - mTLS 설정이 정상 동작하는지
 * - 토스 OAuth 토큰 교환이 되는지
 * 를 검증하기 위한 엔드포인트입니다.
 */
@RestController
@RequestMapping("/api/toss")
@RequiredArgsConstructor
public class TossTestController {

    private final TossApiService tossApiService;

    /**
     * 토스 로그인 토큰 교환 테스트
     *
     * 1. 앱인토스/토스 앱에서 authorizationCode를 받은 뒤,
     * 2. 아래 엔드포인트를 호출하여 accessToken 교환 여부를 확인합니다.
     *
     * 예시:
     * GET /api/toss/login-test?code=AUTH_CODE
     */
    @GetMapping("/login-test")
    public ResponseEntity<TossLoginResponse> loginTest(@RequestParam("code") String authorizationCode) {
        TossLoginResponse response = tossApiService.login(authorizationCode);
        return ResponseEntity.ok(response);
    }
}


