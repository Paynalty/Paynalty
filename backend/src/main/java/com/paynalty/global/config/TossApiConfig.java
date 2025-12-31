package com.paynalty.global.config;

import com.paynalty.global.toss.TLSClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.net.ssl.SSLContext;

@Slf4j
@Getter
@Configuration
public class TossApiConfig {

    private final ResourceLoader resourceLoader;

    @Value("${toss.api.key}")
    private String apiKey;

    public TossApiConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public SSLContext tossSslContext(
            @Value("${toss.api.mtls.client-cert-path}") String certPath,
            @Value("${toss.api.mtls.client-key-path}") String keyPath
    ) throws Exception {
        Resource certResource = resourceLoader.getResource(certPath);
        Resource keyResource = resourceLoader.getResource(keyPath);

        return TLSClient.createSSLContext(certResource, keyResource);
    }
}

