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
}
