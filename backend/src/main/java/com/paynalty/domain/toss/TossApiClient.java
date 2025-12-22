package com.paynalty.domain.toss;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class TossApiClient {

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

    public static String getJson(String url, SSLContext sslContext, String bearerToken) throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
        conn.setSSLSocketFactory(sslContext.getSocketFactory());
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        if (bearerToken != null && !bearerToken.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + bearerToken);
        }
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        return readResponse(conn);
    }

    private static String readResponse(HttpsURLConnection conn) throws IOException {
        try {
            InputStream is = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } finally {
            conn.disconnect();
        }
    }
}
