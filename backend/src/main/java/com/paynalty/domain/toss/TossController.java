package com.paynalty.domain.toss;

import com.paynalty.domain.toss.dto.TossLoginRequest;
import com.paynalty.domain.toss.dto.TossTokenResponse;
import com.paynalty.domain.toss.dto.UserLoginResponse;
import com.paynalty.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/toss")
public class TossController {

    private final TossApiClient tossApiClient;

    @Tag(name = "Toss Token Response Verifier", description = "generate-token 응답")
    @PostMapping("/token-response")
    public Mono<ResponseEntity<TossTokenResponse>> verifyTokenResponse(@RequestBody TossLoginRequest loginRequest) {
        return tossApiClient.fetchToken(loginRequest.getAuthorizationCode(), loginRequest.getReferrer()) // Call fetchToken
                .map(tokenResponse -> ResponseEntity.ok(tokenResponse));
    }
}
