package com.paynalty.domain.toss;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paynalty.domain.toss.dto.TossLoginRequest;
import com.paynalty.domain.toss.dto.TossLoginResponse;
import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserService;
import com.paynalty.global.security.jwt.JwtProvider;
import com.paynalty.global.security.jwt.dto.JwtToken;
import com.paynalty.global.toss.TossDataDecryptor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth/toss")
@RequiredArgsConstructor
@Tag(name = "Toss Auth API", description = "토스 인증 API")
public class TossLoginController {

    private final TossApiClient tossApiClient;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final TossDataDecryptor tossDataDecryptor;
    private final JwtProvider jwtProvider;

    @Operation(summary = "토스 로그인", description = "토스 OAuth 인가 코드로 accessToken 발급 후 로그인")
    @PostMapping("/login")
    public ResponseEntity<JwtToken> handleTossLogin(@RequestBody TossLoginRequest loginRequest) {
        try {
            log.info("토스 로그인 요청: authorizationCode={}", loginRequest.getAuthorizationCode());

            // 1. 토스 API 호출하여 토큰 발급
            String tossApiResponse = tossApiClient.fetchToken(
                    loginRequest.getAuthorizationCode(),
                    loginRequest.getReferrer());

            log.debug("토스 API 응답: {}", tossApiResponse);

            return processTossTokenResponse(tossApiResponse);

        } catch (Exception e) {
            log.error("토스 로그인 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    private ResponseEntity<JwtToken> processTossTokenResponse(String tossApiResponse) throws Exception {
        // 2. JSON 응답 파싱 (토큰 발급 응답)
        JsonNode responseNode = objectMapper.readTree(tossApiResponse);

        // API 응답 실패 처리
        if (responseNode.has("resultType") && "FAIL".equals(responseNode.get("resultType").asText())) {
            JsonNode errorNode = responseNode.get("error");
            log.error("토스 API 토큰 발급 실패: errorCode={}, reason={}",
                    errorNode.get("errorCode").asText(),
                    errorNode.get("reason").asText());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(null);
        }

        JsonNode successNode = responseNode.get("success");

        if (successNode == null || successNode.isNull()) {
            log.error("토스 API 응답에 success 필드가 없거나 null입니다: {}", tossApiResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

        TossLoginResponse tossTokenResponse = objectMapper.treeToValue(successNode, TossLoginResponse.class);
        String tossAccessToken = tossTokenResponse.getAccessToken(); // Toss API 접근 토큰
        log.info("토스 로그인 성공: Toss AccessToken 발급 완료");

        // 3. Toss AccessToken으로 사용자 정보 조회하여 userKey 얻기
        String userInfoResponse = tossApiClient.fetchUserInfo(tossAccessToken);
        log.debug("사용자 정보 API 응답: {}", userInfoResponse);

        JsonNode userInfoNode = objectMapper.readTree(userInfoResponse);
        JsonNode userInfoSuccessNode = userInfoNode.get("success");

        if (userInfoSuccessNode == null) {
            log.error("사용자 정보 API 응답에 success 필드가 없습니다: {}", userInfoResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

        Long userKey = userInfoSuccessNode.get("userKey").asLong();
        log.info("사용자 정보 조회 완료: userKey={}", userKey);

        // 4. 암호화된 사용자 정보 복호화
        String encryptedName = userInfoSuccessNode.has("name") && !userInfoSuccessNode.get("name").isNull()
                ? userInfoSuccessNode.get("name").asText()
                : null;
        String encryptedPhone = userInfoSuccessNode.has("phone") && !userInfoSuccessNode.get("phone").isNull()
                ? userInfoSuccessNode.get("phone").asText()
                : null;
        String encryptedEmail = userInfoSuccessNode.has("email") && !userInfoSuccessNode.get("email").isNull()
                ? userInfoSuccessNode.get("email").asText()
                : null;

        String decryptedName = null;
        String decryptedPhone = null;
        String decryptedEmail = null;

        try {
            if (encryptedName != null) {
                decryptedName = tossDataDecryptor.decrypt(encryptedName);
                log.debug("이름 복호화 완료");
            }
            if (encryptedPhone != null) {
                decryptedPhone = tossDataDecryptor.decrypt(encryptedPhone);
                log.debug("전화번호 복호화 완료");
            }
            if (encryptedEmail != null) {
                decryptedEmail = tossDataDecryptor.decrypt(encryptedEmail);
                log.debug("이메일 복호화 완료");
            }
        } catch (Exception e) {
            log.error("사용자 정보 복호화 실패: userKey={}, error={}", userKey, e.getMessage(), e);
        }

        // 5. 토큰을 User 엔티티에 저장 (User가 없으면 생성, Toss Refresh Token 저장)
        userService.saveTokens(
                userKey,
                tossTokenResponse.getRefreshToken());
        log.info("토큰 저장 완료: userKey={}", userKey);

        // 6. 복호화된 사용자 정보 저장/업데이트
        if (decryptedName != null || decryptedPhone != null || decryptedEmail != null) {
            userService.saveUserInfo(userKey, decryptedName, decryptedPhone, decryptedEmail);
            log.info("사용자 정보 저장 완료: userKey={}, userId={}", userKey);
        }

        // 7. JWT 토큰 발급 (앱 자체 인증 토큰 - App Access Token)
        // Toss Access Token과 혼동하지 마세요. 클라이언트는 이 토큰을 Authorization 헤더에 사용합니다.
        String appAccessToken = jwtProvider.createToken(userKey);
        JwtToken jwtToken = JwtToken.builder()
                .grantType("Bearer")
                .accessToken(appAccessToken)
                .build();
        log.info("App JWT 토큰 발급 완료: appAccessToken={}", appAccessToken);

        return ResponseEntity.ok(jwtToken);
    }

    @Operation(summary = "회원 탈퇴 (로그인 연결 끊기)", description = "Toss 로그인 연결을 끊고 회원 정보를 삭제")
    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // JWT의 subject (TossId)
        Long tossId = Long.valueOf(principal.getName());
        User user = userService.getByTossId(tossId);

        userService.withdraw(user.getId());

        return ResponseEntity.ok().build();
    }
}
