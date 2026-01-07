package com.paynalty.domain.penalty;

import com.paynalty.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Penalty", description = "벌금 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/penalties")
public class PenaltyController {

    private final PenaltyService penaltyService;

    @Operation(
            summary = "본인 벌금 내역 조회",
            description = "특정 챌린지에서 본인이 받은 벌금 내역을 조회합니다.\n\n" +
                    "🔐 JWT 토큰 인증 필수 (Authorization: Bearer {token})\n" +
                    "📌 최신순으로 정렬되어 반환됩니다."
    )
    @GetMapping("/{challengeId}")
    public ResponseEntity<List<PenaltyResponse>> getMyPenalties(
            @Parameter(description = "챌린지 ID", required = true, example = "1")
            @PathVariable Long challengeId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long tossId = userDetails.getUser().getTossId();
        List<PenaltyResponse> response = penaltyService.getMyPenalty(challengeId, tossId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "챌린지의 모든 벌금 내역 조회",
            description = "특정 챌린지에서 발생한 모든 참여자들의 벌금 내역을 조회합니다.\n\n" +
                    "🔐 JWT 토큰 인증 필수 (Authorization: Bearer {token})\n" +
                    "📌 요청한 사용자가 해당 챌린지에 참여하고 있어야 합니다.\n" +
                    "📌 최신순으로 정렬되어 반환됩니다."
    )
    @GetMapping("/{challengeId}/all")
    public ResponseEntity<List<PenaltyResponse>> getAllPenalties(
            @Parameter(description = "챌린지 ID", required = true, example = "1")
            @PathVariable Long challengeId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long tossId = userDetails.getUser().getTossId();
        List<PenaltyResponse> response = penaltyService.getAllPenalties(challengeId, tossId);
        return ResponseEntity.ok(response);
    }
}
