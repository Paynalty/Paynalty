package com.paynalty.domain.toss;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paynalty.domain.toss.dto.TossLoginRequest;
import com.paynalty.domain.toss.dto.TossLoginResponse;
import com.paynalty.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/toss")
@RequiredArgsConstructor
public class TossLoginController {

    private final TossApiClient tossApiClient;
    private final UserService userService;
    private final ObjectMapper objectMapper;


    @PostMapping("/login")
    public ResponseEntity<TossLoginResponse> handleTossLogin(@RequestBody TossLoginRequest loginRequest) {
        try {
            log.info("토스 로그인 요청: authorizationCode={}", loginRequest.getAuthorizationCode());

            // 1. 토스 API 호출하여 토큰 발급
            String tossApiResponse = tossApiClient.fetchToken(
                    loginRequest.getAuthorizationCode(),
                    loginRequest.getReferrer()
            );

            log.debug("토스 API 응답: {}", tossApiResponse);

                        // 2. JSON 응답 파싱 (토큰 발급 응답)
                        JsonNode responseNode = objectMapper.readTree(tossApiResponse);
            
                        // API 응답 실패 처리
                        if (responseNode.has("resultType") && "FAIL".equals(responseNode.get("resultType").asText())) {
                            JsonNode errorNode = responseNode.get("error");
                            log.error("토스 API 토큰 발급 실패: errorCode={}, reason={}",
                                    errorNode.get("errorCode").asText(),
                                    errorNode.get("reason").asText());
                            return ResponseEntity.status(HttpStatus.BAD_GATEWAY) // 502 Bad Gateway or other appropriate error
                                    .body(null);
                        }
            
                        JsonNode successNode = responseNode.get("success");
            
                        if (successNode == null || successNode.isNull()) {
                            log.error("토스 API 응답에 success 필드가 없거나 null입니다: {}", tossApiResponse);
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(TossLoginResponse.builder().build());
                        }
            
                        TossLoginResponse loginResponse = objectMapper.treeToValue(successNode, TossLoginResponse.class);
                        log.info("토스 로그인 성공: accessToken 발급 완료");
            // 3. AccessToken으로 사용자 정보 조회하여 userKey 얻기
            String userInfoResponse = tossApiClient.fetchUserInfo(loginResponse.getAccessToken());
            log.debug("사용자 정보 API 응답: {}", userInfoResponse);
            
            JsonNode userInfoNode = objectMapper.readTree(userInfoResponse);
            JsonNode userInfoSuccessNode = userInfoNode.get("success");
            
            if (userInfoSuccessNode == null) {
                log.error("사용자 정보 API 응답에 success 필드가 없습니다: {}", userInfoResponse);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(TossLoginResponse.builder().build());
            }
            
            Long userKey = userInfoSuccessNode.get("userKey").asLong();
            log.info("사용자 정보 조회 완료: userKey={}", userKey);

            // 4. 토큰을 User 엔티티에 저장 (User가 없으면 생성)
            userService.saveTokens(
                    userKey,
                    loginResponse.getAccessToken(),
                    loginResponse.getRefreshToken()
            );
            log.info("토큰 저장 완료: userKey={}", userKey);

            return ResponseEntity.ok(loginResponse);

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("토스 API 응답 파싱 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(TossLoginResponse.builder().build());
        } catch (Exception e) {
            log.error("토스 로그인 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(TossLoginResponse.builder().build());
        }
    }
}
