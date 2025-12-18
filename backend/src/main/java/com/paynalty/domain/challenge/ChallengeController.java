package com.paynalty.domain.challenge;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @PostMapping
    public ResponseEntity<ChallengeResponse> createChallenge(
            @Parameter(description = "챌린지 생성 요청 정보", required = true)
            @Valid @RequestBody ChallengeRequest request) {
        ChallengeResponse response = challengeService.create(request);
        return ResponseEntity.ok(response);
    }

    // 사용자가 참여중인 챌린지 중 챌린지 상태(인증,미인증)에 따른 챌린지 목록 요청
    @GetMapping("/{userId}/{status}")
    public ResponseEntity<List<ChallengeResponse>> findByStatus(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "챌린지 상태", required = true, example = "진행중")
            @PathVariable String status
    ) {
        List<ChallengeResponse> response = challengeService.findByStatus(userId, status);
        return ResponseEntity.ok(response);
    }
}

