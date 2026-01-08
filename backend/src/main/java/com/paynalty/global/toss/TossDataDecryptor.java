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
            byte[] decoded = Base64.getDecoder().decode(encryptedText);

            if (decoded.length <= IV_LENGTH) {
                throw new IllegalArgumentException("Invalid encrypted text: length is too short.");
            }

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] keyByteArray = Base64.getDecoder().decode(base64EncodedAesKey);
            SecretKeySpec key = new SecretKeySpec(keyByteArray, "AES");

            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, decoded, 0, IV_LENGTH);

            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
            cipher.updateAAD(AAD.getBytes());

            byte[] decrypted = cipher.doFinal(decoded, IV_LENGTH, decoded.length - IV_LENGTH);
            return new String(decrypted);

        } catch (Exception e) {
            log.error("토스 데이터 복호화 실패: encryptedText={}, error={}", encryptedText, e.getMessage());
            throw new RuntimeException("토스 데이터 복호화 실패: " + e.getMessage(), e);
        }
    }
}

