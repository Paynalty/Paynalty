package com.paynalty.domain.challengeverification;


import com.paynalty.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ChallengeVerification")
public class ChallengeVerificationController {
    private final ChallengeVerificationService challengeVerificationService;

    @PostMapping("/{challengeId}/{userId}")
    public ResponseEntity<ApiResponse<ChallengeVerificationResponse>> create(
            @RequestBody ChallengeVerificationRequest request,
            @PathVariable Long challengeId,
            @PathVariable Long userId
    ){
        ChallengeVerificationResponse response = challengeVerificationService.create(challengeId,userId,request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
