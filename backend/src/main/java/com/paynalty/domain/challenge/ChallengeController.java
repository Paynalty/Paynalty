package com.paynalty.domain.challenge;

import com.paynalty.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
            description = "새로운 챌린지를 생성합니다.\n"
                    + "로그인 기능 구현 전까지는 임시 사용자(userId=1)로 처리됩니다."
    )
    @PostMapping()
    public ResponseEntity<ApiResponse<ChallengeResponse>> createChallenge(
            @Parameter(description = "챌린지 생성 요청 정보", required = true)
            @Valid @RequestBody ChallengeRequest request) {
        Long userId = 1L;
        ChallengeResponse response = challengeService.create(request,userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 사용자가 참여중인 챌린지 중 챌린지 상태(인증,미인증)에 따른 챌린지 목록 요청
    @Operation(
            summary = "진행 상황에 따른 챌린지 목록 불러오기",
            description = "시작전 챌린지 : pending , 진행중 챌린지 : progress "
    )
    @GetMapping("/{userId}/{status}")
    public ResponseEntity<ApiResponse<List<ChallengeResponse>>> getByStatus(
            @Parameter(description = "사용자 userId", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "챌린지 상태", required = true, example = "progress")
            @PathVariable String status
    ) {
        List<ChallengeResponse> response = challengeService.findByStatus(userId , status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 필요없어서 수정할거. getMyProgressChallengesDetail와 비슷한 기능. -> 챌린지 진행 상황 파악을 위한 내용을 변경할 예정
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
            summary = "진행중인 챌린지 목록 상세 정보 조회",
            description = "사용자가 참여 중인 진행중인 챌린지 목록을 조회하고, 각 챌린지의 일부 정보를 반환\n" +
                    "각 챌린지의 주간 인증 현황, 벌금, 남은 시간 정보를 포함"
    )
    @GetMapping("/myProgressChallenges/{userId}")
    public ResponseEntity<ApiResponse<List<ChallengeDetailResponse>>> getMyProgressChallengesDetail(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @PathVariable Long userId
    ) {
        List<ChallengeDetailResponse> response = challengeService.getMyProgressChallengesDetail(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}

