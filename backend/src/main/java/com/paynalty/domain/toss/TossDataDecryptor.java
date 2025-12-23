package com.paynalty.domain.toss;

import com.paynalty.global.config.TossApiConfig;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Base64;

@Component
public class TossDataDecryptor {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_BIT_LENGTH = 128;
    private static final int NONCE_LENGTH = 12;

    private final byte[] secretKey;

    public TossDataDecryptor(TossApiConfig tossApiConfig) {
        this.secretKey = Base64.getDecoder().decode(tossApiConfig.getDataSecretKey());
        // BouncyCastle Provider를 한 번만 등록하도록
        if(Security.getAlgorithms("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public String decrypt(String encryptedData) {
        try {
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            byte[] nonce = new byte[NONCE_LENGTH];
            System.arraycopy(decodedData, 0, nonce, 0, NONCE_LENGTH);

            byte[] cipherText = new byte[decodedData.length - NONCE_LENGTH];
            System.arraycopy(decodedData, NONCE_LENGTH, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_BIT_LENGTH, nonce);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

            byte[] decryptedBytes = cipher.doFinal(cipherText);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Toss 사용자 정보 복호화에 실패", e);
        }
    }
}
