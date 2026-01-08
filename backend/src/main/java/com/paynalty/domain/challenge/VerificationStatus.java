package com.paynalty.domain.challenge;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 챌린지 인증 상태
 */
@Schema(description = "챌린지 인증 상태")
public enum VerificationStatus {
    
    @Schema(description = "인증 완료")
    VERIFIED("인증함", "Verified"),
    
    @Schema(description = "인증 안함")
    NOT_VERIFIED("인증안함", "Not Verified");

    private final String koreanName;
    private final String englishName;

    VerificationStatus(String koreanName, String englishName) {
        this.koreanName = koreanName;
        this.englishName = englishName;
    }

    /**
     * 한글명 반환
     */
    public String getKoreanName() {
        return koreanName;
    }

    /**
     * 영어명 반환
     */
    public String getEnglishName() {
        return englishName;
    }

    /**
     * 인증 완료 여부 확인
     */
    public boolean isVerified() {
        return this == VERIFIED;
    }

    /**
     * 인증 여부에 따라 적절한 상태 반환
     * 
     * @param isVerified 인증 완료 여부
     * @return 해당하는 VerificationStatus
     */
    public static VerificationStatus from(boolean isVerified) {
        return isVerified ? VERIFIED : NOT_VERIFIED;
    }
}

