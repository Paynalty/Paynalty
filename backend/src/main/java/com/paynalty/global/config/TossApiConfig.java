package com.paynalty.global.config;

import com.paynalty.global.toss.TLSClient;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Slf4j
@Configuration
public class TossApiConfig {

    private final ResourceLoader resourceLoader;

    public TossApiConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public io.netty.handler.ssl.SslContext tossSslContext(
            @Value("${toss.api.mtls.client-cert-path}") String certPath,
            @Value("${toss.api.mtls.client-key-path}") String keyPath
    ) throws Exception {
        Resource certResource = resourceLoader.getResource(certPath);
        Resource keyResource = resourceLoader.getResource(keyPath);

        X509Certificate certificate = TLSClient.loadCertificate(certResource);
        PrivateKey privateKey = TLSClient.loadPrivateKey(keyResource);

        return SslContextBuilder.forClient()
                .keyManager(privateKey, certificate)
                .build();
    }

    @Bean
    @Qualifier("tossWebClient")
    public WebClient tossWebClient(
            io.netty.handler.ssl.SslContext tossSslContext,
            @Value("${toss.api.base-url}") String baseUrl
    ) {
        HttpClient httpClient = HttpClient.create()
                .secure(t -> t.sslContext(tossSslContext));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}

