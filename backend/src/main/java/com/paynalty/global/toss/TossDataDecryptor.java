package com.paynalty.global.toss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
@Component
public class TossDataDecryptor {

    private static final int IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final String AAD = "TOSS";
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    private final String base64EncodedAesKey;

    public TossDataDecryptor(String base64EncodedAesKey) {
        this.base64EncodedAesKey = base64EncodedAesKey;
    }


    public String decrypt(String encryptedText) throws Exception {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return null;
        }

        try {
            // Base64 디코딩
            byte[] decoded = Base64.getDecoder().decode(encryptedText);

            // IV 추출 (앞부분 12바이트)
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(decoded, 0, iv, 0, IV_LENGTH);

            // 암호문 추출 (IV 이후 부분)
            byte[] ciphertext = new byte[decoded.length - IV_LENGTH];
            System.arraycopy(decoded, IV_LENGTH, ciphertext, 0, ciphertext.length);

            // AES 키 생성
            byte[] keyByteArray = Base64.getDecoder().decode(base64EncodedAesKey);
            SecretKeySpec key = new SecretKeySpec(keyByteArray, "AES");

            // GCM 파라미터 설정
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

            // 복호화 수행
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
            cipher.updateAAD(AAD.getBytes());

            byte[] decrypted = cipher.doFinal(ciphertext);  
            return new String(decrypted);

        } catch (Exception e) {
            log.error("토스 데이터 복호화 실패: encryptedText={}, error={}", encryptedText, e.getMessage());
            throw new RuntimeException("토스 데이터 복호화 실패: " + e.getMessage(), e);
        }
    }
}

