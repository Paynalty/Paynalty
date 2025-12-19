package com.paynalty.global.config;

import com.paynalty.global.toss.TLSClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;

@Slf4j
@Configuration
public class TossApiConfig {


    @Bean
    public SSLContext tossSslContext(
            @Value("${toss.api.mtls.client-cert-path}") String certPath,
            @Value("${toss.api.mtls.client-key-path}") String keyPath
    ) throws Exception {
        return TLSClient.createSSLContext(certPath, keyPath);
    }

}

