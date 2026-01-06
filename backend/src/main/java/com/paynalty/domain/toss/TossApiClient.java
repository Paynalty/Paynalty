package com.paynalty.domain.toss;

import com.paynalty.global.config.TossApiConfig;
import com.paynalty.global.toss.TLSClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossApiClient {

        private final SSLContext tossSslContext;
        private final TossApiConfig tossApiConfig;
        private final ObjectMapper objectMapper;

        public String fetchToken(String authorizationCode, String referrer) throws Exception {
                String credentials = tossApiConfig.getApiKey() + ":";
                String basicAuthHeader = "Basic "
                                + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
                Map<String, String> headers = Map.of("Authorization", basicAuthHeader);

                String jsonBody = objectMapper.writeValueAsString(Map.of(
                                "authorizationCode", authorizationCode,
                                "referrer", referrer));

                return TLSClient.postJson(
                                "https://apps-in-toss-api.toss.im/api-partner/v1/apps-in-toss/user/oauth2/generate-token",
                                tossSslContext,
                                jsonBody,
                                headers);
        }

        public String fetchUserInfo(String accessToken) throws Exception {
                String bearerAuthHeader = "Bearer " + accessToken;
                Map<String, String> headers = Map.of("Authorization", bearerAuthHeader);

                return TLSClient.makeRequest(
                                "https://apps-in-toss-api.toss.im/api-partner/v1/apps-in-toss/user/oauth2/login-me",
                                tossSslContext,
                                headers);
        }

        public String refreshToken(String refreshToken) throws Exception {
                String url = "https://apps-in-toss-api.toss.im/api-partner/v1/apps-in-toss/user/oauth2/refresh-token";

                String jsonBody = objectMapper.writeValueAsString(Map.of("refreshToken", refreshToken));

                return TLSClient.postJson(
                                url,
                                tossSslContext,
                                jsonBody,
                                java.util.Collections.emptyMap());
        }

        public String unlink(String accessToken) throws Exception {
                String url = "https://apps-in-toss-api.toss.im/api-partner/v1/apps-in-toss/user/oauth2/access/remove-by-access-token";
                String bearerAuthHeader = "Bearer " + accessToken;
                Map<String, String> headers = Map.of("Authorization", bearerAuthHeader);

                return TLSClient.postJson(
                                url,
                                tossSslContext,
                                "{}",
                                headers);
        }

        public String removeByUserKey(Long userKey) throws Exception {
                String url = "https://apps-in-toss-api.toss.im/api-partner/v1/apps-in-toss/user/oauth2/access/remove-by-user-key";

                // Basic Auth
                String credentials = tossApiConfig.getApiKey() + ":";
                String basicAuthHeader = "Basic "
                                + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
                Map<String, String> headers = Map.of("Authorization", basicAuthHeader);

                // Body: {"userKey": ...}
                String jsonBody = objectMapper.writeValueAsString(Map.of("userKey", userKey));

                return TLSClient.postJson(
                                url,
                                tossSslContext,
                                jsonBody,
                                headers);
        }
}