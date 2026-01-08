package com.paynalty.domain.challenge;

import com.paynalty.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Challenge", description = "챌린지 관리 API")
@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @Operation(
            summary = "챌린지 생성",
            description = "새로운 챌린지를 생성합니다.\n\n" +
                    "📌 주요 규칙:\n" +
                    "1. startDate: 오늘보다 이후여야 함 (오늘 포함 불가)\n" +
                    "2. endDate: startDate보다 이후여야 함\n" +
                    "3. dayOfWeeks와 frequency: 둘 중 하나만 사용\n" +
                    "   - dayOfWeeks가 있으면 → frequency는 자동 계산 (요일 개수)\n" +
                    "   - dayOfWeeks가 null이거나 빈 리스트이면 → frequency 값 필수\n" +
                    "4. verifyStartAt/verifyEndAt: 둘 다 null이면 기본값(00:00, 23:59) 사용\n" +
                    "5. verifyStartAt은 verifyEndAt보다 이전이어야 함\n\n" +
                    "✅ 성공 시: 201 Created + 생성된 챌린지 ID 반환\n" +
                    "💡 상세 정보 조회는 GET /api/challenge/{challengeId}/detail 사용\n" +
                    "🔐 JWT 토큰 인증 필수 (Authorization: Bearer {token})"
    )
    @PostMapping()
    public ResponseEntity<Long> createChallenge(
            @Parameter(description = "챌린지 생성 요청 정보", required = true)
            @Valid @RequestBody ChallengeRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long tossId = userDetails.getUser().getTossId();
        Long challengeId = challengeService.create(request, tossId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(challengeId);
    }

    @Operation(
            summary = "진행 상황에 따른 챌린지 목록 불러오기",
            description = "시작전 챌린지 : PENDING , 진행중 챌린지 : ACTIVE, 완료된 챌린지 : COMPLETE\n\n" +
                    "🔐 JWT 토큰 인증 필수 (Authorization: Bearer {token})"
    )
    @GetMapping("/{status}")
    public ResponseEntity<List<ChallengeResponse>> getByStatus(
            @Parameter(description = "챌린지 상태", required = true, example = "PENDING")
            @PathVariable ChallengeStatus status,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long tossId = userDetails.getUser().getTossId();
        List<ChallengeResponse> response = challengeService.findDetailByStatus(tossId, status);
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "챌린지 상세 정보 조회",
            description = "챌린지의 상세 정보를 조회합니다.\n\n" +
                    "🔐 JWT 토큰 인증 필수 (Authorization: Bearer {token})"
    )
    @GetMapping("/{challengeId}/detail")
    public ResponseEntity<ChallengeDetailResponse> getDetail(
            @PathVariable Long challengeId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long tossId = userDetails.getUser().getTossId();
        ChallengeDetailResponse response = challengeService.getMyChallengeDetail(challengeId, tossId);
        return ResponseEntity.ok(response);
    }


    // 수정을 위해 유저에게 보여줄 UpdateQuest 전달
    @Operation(
            description = "챌린지 수정 페이지 이동시 사용자한테 보여줄 데이터(challengeUpdateRequest(기존 정보값이 설정되있는상태)",
            summary = "수정 화면에서 사용자에게 제공할 필드값이 담긴 객체"
    )
    @GetMapping("/{challengeId}/edit")
    public ResponseEntity<ChallengeUpdateRequest> getEditForm(
        @PathVariable Long challengeId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long tossId = userDetails.getUser().getTossId();
        ChallengeUpdateRequest response = challengeService.getUpdateForm(challengeId, tossId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "챌린지 수정",
            description = "getEditForm에 응답 받은 데이터 토대로 데이터 수정 후 업데이트 요청\n\n" +
                    "🔐 JWT 토큰 인증 필수 (Authorization: Bearer {token})\n" +
                    "⚠️ 챌린지 생성자만 수정 가능"
    )
    @PutMapping("/{challengeId}/")
    public ResponseEntity<ChallengeResponse> updateChallenge(
            @PathVariable Long challengeId,
            @RequestBody ChallengeUpdateRequest updateRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails
    )
    {
        Long tossId = userDetails.getUser().getTossId();
        ChallengeResponse response = challengeService.update(challengeId, updateRequest, tossId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "챌린지 삭제",
            description = "챌린지를 삭제합니다.\n\n" +
                    "🔐 JWT 토큰 인증 필수 (Authorization: Bearer {token})\n" +
                    "⚠️ 챌린지 생성자만 삭제 가능"
    )
    @DeleteMapping("/{challengeId}")
    public ResponseEntity<Void> deleteChallenge(
            @Parameter(description = "삭제할 챌린지 ID", required = true, example = "1")
            @PathVariable Long challengeId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long tossId = userDetails.getUser().getTossId();
        challengeService.delete(challengeId, tossId);
        return ResponseEntity.ok().build();
    }

}
