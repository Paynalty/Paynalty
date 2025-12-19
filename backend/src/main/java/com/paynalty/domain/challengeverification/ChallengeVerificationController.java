package com.paynalty.domain.challengeverification;

import com.paynalty.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Challenge Verification", description = "챌린지 인증 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge-verifications")
public class ChallengeVerificationController {
    private final ChallengeVerificationService challengeVerificationService;

    @PostMapping("/{challengeId}/{authEmail}")
    public ResponseEntity<ApiResponse<ChallengeVerificationResponse>> create(
            @Parameter(description = "챌린지 ID", required = true, example = "1")
            @PathVariable Long challengeId,
            @Parameter(description = "사용자 이메일", required = true, example = "test@test.com")
            @PathVariable String authEmail,
            @Parameter(description = "챌린지 인증 요청 정보", required = true)
            @Valid @RequestBody ChallengeVerificationRequest request
    ) {
        ChallengeVerificationResponse response = challengeVerificationService.create(challengeId, authEmail, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
