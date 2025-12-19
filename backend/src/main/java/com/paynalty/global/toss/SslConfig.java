package com.paynalty.global.toss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;

@Configuration
public class SslConfig {

    @Bean
    public SSLContext tossSslContext(
            @Value("${toss.api.mtls.client-cert-path}") String certPath,
            @Value("${toss.api.mtls.client-key-path}") String keyPath
    ) throws Exception {
        return TLSClient.createSSLContext(certPath, keyPath);
    }

}
