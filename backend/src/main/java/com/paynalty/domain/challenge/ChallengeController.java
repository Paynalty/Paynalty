package com.paynalty.domain.challenge;

import com.paynalty.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Challenge", description = "챌린지 관리 API")
@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    //TODO : 로그인 기능 구현 후 @AuthenticationPrincipal 사용자 정보 불러와서 create 매개변수에 nickName 추가)
    // 현재는 주소에 변수 넣어서 사용중
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
                    "⚠️ 로그인 기능 구현 전까지는 임시 사용자(userId=1)로 처리됩니다."
    )
    @PostMapping()
    public ResponseEntity<ApiResponse<Long>> createChallenge(
            @Parameter(description = "챌린지 생성 요청 정보", required = true)
            @Valid @RequestBody ChallengeRequest request) {
        Long userId = 1L;
        Long challengeId = challengeService.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(challengeId));
    }

    // 사용자가 참여중인 챌린지 중 챌린지 상태(인증,미인증)에 따른 챌린지 목록 요청
    // todo userId -> authId로 교체
    @Operation(
            summary = "진행 상황에 따른 챌린지 목록 불러오기",
            description = "시작전 챌린지 : PENDING , 진행중 챌린지 : ACTIVE, 완료된 챌린지 : COMPLETE"
    )
    @GetMapping("/{userId}/{status}")
    public ResponseEntity<ApiResponse<List<ChallengeDetailResponse>>> getByStatus(
            @Parameter(description = "사용자 userId", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "챌린지 상태", required = true, example = "PENDING")
            @PathVariable ChallengeStatus status
    ) {
        List<ChallengeDetailResponse> response = challengeService.findDetailByStatus(userId , status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 필요없어서 수정할거. getMyProgressChallengesDetail와 비슷한 기능. -> 챌린지 진행 상황 파악을 위한 내용을 변경할 예정
    @Operation(
            summary = "삭제예정"

    )
    @GetMapping("/{challengeId}/myChallenge/detail")
    public ResponseEntity<ApiResponse<ChallengeDetailResponse>> detail(
            @Parameter(description = "챌린지 ID", required = true, example = "1")
            @PathVariable Long challengeId
    ) {
        // TODO: 로그인 기능 구현 후 @AuthenticationPrincipal 사용자 정보 불러와서 userId 사용
        // 현재는 임시로 userId = 1L 사용
        Long userId = 1L;
        
        ChallengeDetailResponse response = challengeService.getMyChallengeDetail(challengeId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @Operation(
            summary = "삭제 예정",
            description = "삭제 예정"

    )
    @GetMapping("/{challengeId}/detail")
    public ResponseEntity<ApiResponse<ChallengeResponse>> getChallengeDetail(
            @Parameter(description = "챌린지 ID", required = true, example = "1")
            @PathVariable Long challengeId
    ) {
        // TODO: 로그인 기능 구현 후 @AuthenticationPrincipal 사용자 정보 불러와서 userId 사용
        // 현재는 임시로 userId = 1L 사용
        Long userId = 1L;
        
        ChallengeResponse response = challengeService.getChallengeDetail(challengeId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 수정을 위해 유저에게 보여줄 UpdateQuest 전달

    // 전달 받은 데이터에서 수정 후 편집

}
