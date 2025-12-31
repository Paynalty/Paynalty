package com.paynalty.domain.challengeverification;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "챌린지 인증 수정 요청")
@Getter
@Setter
@NoArgsConstructor
public class ChallengeVerificationUpdateRequest {

    @Schema(description = "수정할 인증 이미지 URL", example = "https://example.com/new-image.jpg", required = true)
    @NotBlank(message = "인증 이미지 URL은 필수입니다")
    @Size(max = 500, message = "인증 이미지 URL은 500자 이하여야 합니다")
    private String imageUrl;

}

