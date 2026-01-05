package com.paynalty.domain.toss;

import com.paynalty.global.config.TossApiConfig;
import com.paynalty.global.toss.TLSClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TossApiClient {

    private final SSLContext tossSslContext;
    private final TossApiConfig tossApiConfig;

    public String fetchToken(String authorizationCode, String referrer) throws Exception {
        String credentials = tossApiConfig.getApiKey() + ":";
        String basicAuthHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        Map<String, String> headers = Map.of("Authorization", basicAuthHeader);

        String jsonBody = String.format(
                "{\"authorizationCode\":\"%s\",\"referrer\":\"%s\"}",
                authorizationCode,
                referrer
        );

        return TLSClient.postJson(
                "https://apps-in-toss-api.toss.im/api-partner/v1/apps-in-toss/user/oauth2/generate-token",
                tossSslContext,
                jsonBody,
                headers
        );
    }

    public String fetchUserInfo(String accessToken) throws Exception {
        String bearerAuthHeader = "Bearer " + accessToken;
        Map<String, String> headers = Map.of("Authorization", bearerAuthHeader);

        return TLSClient.makeRequest(
                "https://apps-in-toss-api.toss.im/api-partner/v1/apps-in-toss/user/oauth2/login-me",
                tossSslContext,
                headers
        );
    }

    public String refreshToken(String refreshToken) throws Exception {
        String url = "https://apps-in-toss-api.toss.im/api-partner/v1/apps-in-toss/user/oauth2/refresh-token";

        String jsonBody = String.format("{\"refreshToken\":\"%s\"}", refreshToken);

        return TLSClient.postJson(
                url,
                tossSslContext,
                jsonBody,
                java.util.Collections.emptyMap()
        );
    }
}