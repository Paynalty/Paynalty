package com.paynalty.global.toss;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;

@RequiredArgsConstructor
@Service
public class TossService {

    private final SSLContext sslContext;

    public String refreshToken() throws Exception {
        String body = """
        {
          "refreshToken": "REPLACE_WITH_REAL_TOKEN"
        }
        """;

        return TLSClient.postJson(
                "https://apps-in-toss-api.toss.im/api-partner/v1/apps-in-toss/user/oauth2/refresh-token",
                sslContext,
                body
        );
    }
}
