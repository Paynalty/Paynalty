package com.paynalty.domain.challenge;

import com.paynalty.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Challenge", description = "챌린지 관리 API")
@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    //TODO : 로그인 기능 구현 후 @AuthenticationPrincipal 사용자 정보 불러와서 create 매개변수에 nickName 추가)
    // 현재는 주소에 변수 넣어서 사용중
    @PostMapping()
    public ResponseEntity<ApiResponse<ChallengeResponse>> createChallenge(
            @Parameter(description = "챌린지 생성 요청 정보", required = true)
            @Valid @RequestBody ChallengeRequest request) {
        Long userId = 1L;
        ChallengeResponse response = challengeService.create(request,userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 사용자가 참여중인 챌린지 중 챌린지 상태(인증,미인증)에 따른 챌린지 목록 요청
    @GetMapping("/{userId}/{status}")
    public ResponseEntity<ApiResponse<List<ChallengeResponse>>> findByStatus(
            @Parameter(description = "사용자 userId", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "챌린지 상태", required = true, example = "progress")
            @PathVariable String status
    ) {
        List<ChallengeResponse> response = challengeService.findByStatus(userId , status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}

