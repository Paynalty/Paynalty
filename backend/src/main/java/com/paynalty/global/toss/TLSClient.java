package com.paynalty.global.toss;

import org.springframework.core.io.Resource;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

public class TLSClient {

    public static SSLContext createSSLContext(Resource certResource, Resource keyResource) throws Exception {
        X509Certificate cert = loadCertificate(certResource);
        PrivateKey key = loadPrivateKey(keyResource);

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);

        keyStore.setCertificateEntry("client-cert", cert);
        keyStore.setKeyEntry(
                "client-key",
                key,
                new char[0],
                new java.security.cert.Certificate[]{cert}
        );

        KeyManagerFactory kmf =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, new char[0]);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        return sslContext;
    }

    private static X509Certificate loadCertificate(Resource resource) throws Exception {
        String content = readResource(resource)
                .replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(content);

        return (X509Certificate)
                CertificateFactory.getInstance("X.509")
                        .generateCertificate(new ByteArrayInputStream(decoded));
    }

    private static PrivateKey loadPrivateKey(Resource resource) throws Exception {
        String content = readResource(resource)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(content);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);

        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private static String readResource(Resource resource) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    // ========== HTTP ==========
    // mTLS가 적용된 HTTPS POST 요청으로 JSON을 보내고, 응답을 문자열로 받는 메서드
    public static String postJson(String url, SSLContext sslContext, String jsonBody)
            throws Exception {

        // HTTPS 연결 생성
        HttpsURLConnection conn =
                (HttpsURLConnection) new URL(url).openConnection();

        // mTLS용 SSLContext 설정 (클라이언트 인증서 포함)
        conn.setSSLSocketFactory(sslContext.getSocketFactory());
        // HTTP Method: POST
        conn.setRequestMethod("POST");
        // 요청 Body 사용 선언 (POST이므로 필요)
        conn.setDoOutput(true);
        // JSON 요청임을 명시
        conn.setRequestProperty("Content-Type", "application/json");
        // 타임아웃 설정
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        // 요청 Body(JSON) 전송
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }

        // 응답 코드에 따라 정상/에러 스트림 선택
        InputStream is = conn.getResponseCode() >= 400
                ? conn.getErrorStream()
                : conn.getInputStream();

        // 응답 Body를 문자열로 변환
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(is))) {

            return reader.lines().collect(Collectors.joining("\n"));
        }
    }


    public static String makeRequest(String url, SSLContext sslContext) throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
        conn.setSSLSocketFactory(sslContext.getSocketFactory());
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int status = conn.getResponseCode();

        InputStream is = (status >= 200 && status < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            conn.disconnect();
        }
    }


}
