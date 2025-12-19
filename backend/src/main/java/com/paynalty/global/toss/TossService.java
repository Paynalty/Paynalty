package com.paynalty.global.toss;

import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;

@Service
public class TossService {

    private final SSLContext sslContext;

    public TossService(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    public String callTossApi() throws Exception {
        return TLSClient.makeRequest(
                "https://apps-in-toss-api.toss.im",
                sslContext
        );
    }

}
