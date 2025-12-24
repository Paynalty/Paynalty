package com.paynalty.domain.challengeverification;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "챌린지 인증 생성 요청")
@Getter
@Setter
@NoArgsConstructor
public class ChallengeVerificationRequest {

    @Schema(description = "인증 이미지 URL", example = "https://example.com/image.jpg")
    @Size(max = 500, message = "인증 이미지 URL은 500자 이하여야 합니다")
    private String imageUrl;

}

