package com.paynalty.global.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

@Slf4j
@Configuration
public class TossApiConfig {

    @Value("${toss.api.base-url}")
    private String tossApiBaseUrl;

    @Value("${toss.api.mtls.client-cert-path}")
    private Resource clientCertResource;

    @Value("${toss.api.mtls.client-key-path}")
    private Resource clientKeyResource;

    @Value("${toss.api.mtls.key-password:}")
    private String keyPassword;

    /**
     * BouncyCastle Provider 등록
     * - PEM 형식의 인증서를 읽기 위해 필요합니다.
     */
    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    // mTLS를 위한 SSLContext 생성
    @Bean
    public SSLContext tossSslContext() {
        try {
            log.info("토스 mTLS SSLContext 설정 시작...");

            // 1. 클라이언트 인증서 읽기
            X509Certificate clientCert = readCertificate(clientCertResource);
            log.debug("클라이언트 인증서 로드 완료: {}", clientCert.getSubjectX500Principal());

            // 2. 클라이언트 개인키 읽기
            PrivateKey privateKey = readPrivateKey(clientKeyResource);
            log.debug("클라이언트 개인키 로드 완료");

            // 3. KeyStore 생성 및 클라이언트 인증서/키 저장
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(null, null);
            keyStore.setKeyEntry(
                    "toss-client-key",
                    privateKey,
                    keyPassword.toCharArray(),
                    new X509Certificate[]{clientCert}
            );

            // 4. SSLContext 빌드 (TrustStore는 JDK 기본값 사용)
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadKeyMaterial(keyStore, keyPassword.toCharArray())
                    .build();

            log.info("토스 mTLS SSLContext 설정 완료");
            return sslContext;

        } catch (Exception e) {
            log.error("토스 mTLS SSLContext 설정 실패", e);
            throw new RuntimeException("토스 mTLS 설정 실패: " + e.getMessage(), e);
        }
    }

    /**
     * PEM 파일에서 인증서 읽기
     */
    private X509Certificate readCertificate(Resource resource) throws Exception {
        try (Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            PEMParser parser = new PEMParser(reader);
            Object object = parser.readObject();
            
            if (object instanceof X509CertificateHolder) {
                X509CertificateHolder certHolder = (X509CertificateHolder) object;
                return new JcaX509CertificateConverter()
                        .setProvider("BC")
                        .getCertificate(certHolder);
            }
            throw new IllegalArgumentException("인증서 형식이 올바르지 않습니다.");
        }
    }

    /**
     * PEM 파일에서 개인키 읽기
     */
    private PrivateKey readPrivateKey(Resource resource) throws Exception {
        try (Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            PEMParser parser = new PEMParser(reader);
            Object object = parser.readObject();
            
            if (object instanceof PrivateKeyInfo) {
                PrivateKeyInfo keyInfo = (PrivateKeyInfo) object;
                return new JcaPEMKeyConverter()
                        .setProvider("BC")
                        .getPrivateKey(keyInfo);
            }
            throw new IllegalArgumentException("개인키 형식이 올바르지 않습니다.");
        }
    }

    /**
     * mTLS를 적용한 RestTemplate 생성
     */
    @Bean(name = "tossRestTemplate")
    public RestTemplate tossRestTemplate(SSLContext sslContext) {
        try {
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                    sslContext,
                    (hostname, session) -> true // 호스트명 검증
            );

            HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build();

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .build();

            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
            factory.setConnectionRequestTimeout(java.time.Duration.ofSeconds(10));
            factory.setReadTimeout(java.time.Duration.ofSeconds(30));

            return new RestTemplate(factory);
        } catch (Exception e) {
            log.error("토스 RestTemplate 생성 실패", e);
            throw new RuntimeException("토스 RestTemplate 생성 실패: " + e.getMessage(), e);
        }
    }

}

