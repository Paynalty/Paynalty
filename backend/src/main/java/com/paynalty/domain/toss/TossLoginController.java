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

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            // 1. 토스 API 호출하여 토큰 발급
            String tossApiResponse = tossApiClient.fetchToken(
                    loginRequest.getAuthorizationCode(),
                    loginRequest.getReferrer());

            return processTossTokenResponse(tossApiResponse);

        } catch (Exception e) {
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

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(null);
        }

        JsonNode successNode = responseNode.get("success");

        if (successNode == null || successNode.isNull()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

        TossLoginResponse tossTokenResponse = objectMapper.treeToValue(successNode, TossLoginResponse.class);
        String tossAccessToken = tossTokenResponse.getAccessToken(); // Toss API 접근 토큰

        // 3. Toss AccessToken으로 사용자 정보 조회하여 userKey 얻기
        String userInfoResponse = tossApiClient.fetchUserInfo(tossAccessToken);

        JsonNode userInfoNode = objectMapper.readTree(userInfoResponse);
        JsonNode userInfoSuccessNode = userInfoNode.get("success");

        if (userInfoSuccessNode == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

        Long userKey = userInfoSuccessNode.get("userKey").asLong();

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
            }
            if (encryptedPhone != null) {
                decryptedPhone = tossDataDecryptor.decrypt(encryptedPhone);
            }
            if (encryptedEmail != null) {
                decryptedEmail = tossDataDecryptor.decrypt(encryptedEmail);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

        // 5. 토큰을 User 엔티티에 저장 (User가 없으면 생성, Toss Refresh Token 저장)
        userService.saveTokens(
                userKey,
                tossTokenResponse.getRefreshToken());

        // 6. 복호화된 사용자 정보 저장/업데이트
        if (decryptedName != null || decryptedPhone != null || decryptedEmail != null) {
            userService.saveUserInfo(userKey, decryptedName, decryptedPhone, decryptedEmail);
        }

        // 7. JWT 토큰 발급 (앱 자체 인증 토큰 - App Access Token)
        // Toss Access Token과 혼동하지 마세요. 클라이언트는 이 토큰을 Authorization 헤더에 사용합니다.
        String appAccessToken = jwtProvider.createToken(userKey);
        JwtToken jwtToken = JwtToken.builder()
                .grantType("Bearer")
                .accessToken(appAccessToken)
                .build();

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

    @Operation(summary = "토스 로그인 연동 해제 콜백", description = "사용자가 토스 앱에서 직접 연동 해제 시 호출됨")
    @PostMapping("/callback/unlink")
    public ResponseEntity<Void> handleTossUnlinkCallback(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody JsonNode callbackData) {
        try {
            // 보안을 위한 Basic Auth 검증 (Toss Console에 설정한 값과 일치해야 함)
            String expectedHeader = "Basic "
                    + java.util.Base64.getEncoder().encodeToString((tossApiClient.getApiKey() + ":").getBytes());
            if (authHeader == null || !authHeader.equals(expectedHeader)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if (callbackData.has("userKey")) {
                Long userKey = callbackData.get("userKey").asLong();
                userService.unlinkByTossId(userKey);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
