package com.paynalty.domain.challengeverification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Challenge Verification", description = "챌린지 인증 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge-verifications")
public class ChallengeVerificationController {
    private final ChallengeVerificationService challengeVerificationService;

    @Operation(
            summary = "챌린지 인증 생성",
            description = "특정 챌린지에 대한 인증을 생성합니다.\n\n" +
                    "📌 검증 사항:\n" +
                    "- 챌린지 멤버 여부 확인\n" +
                    "- 챌린지 상태 (ACTIVE만 가능)\n" +
                    "- 인증 시간대 확인\n" +
                    "- 인증 요일 확인 (요일 지정된 경우)\n" +
                    "- 중복 인증 방지 (1일 1회)\n" +
                    "- 주간 인증 횟수 제한\n\n" +
                    "⚠️ 로그인 기능 구현 전까지는 userId를 Query Parameter로 입력받습니다.\n" +
                    "   예: POST /api/challenge-verifications/1?userId=2"
    )
    @PostMapping("/{challengeId}")
    public ResponseEntity<ChallengeVerificationResponse> create(
            @Parameter(description = "챌린지 ID", required = true, example = "1")
            @PathVariable Long challengeId,
            @Parameter(description = "사용자 ID (테스트용, 로그인 후 제거 예정)", required = false, example = "1")
            @RequestParam(required = false, defaultValue = "1") Long userId,
            @Parameter(description = "챌린지 인증 요청 정보", required = true)
            @Valid @RequestBody ChallengeVerificationRequest request
    ) {
        ChallengeVerificationResponse response = challengeVerificationService.create(challengeId, userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "챌린지의 최신 인증 데이터 조회",
            description = "해당 챌린지의 모든 인증 데이터 중 가장 최근 인증 기록을 조회합니다."
    )
    @GetMapping("/{challengeId}/latest")
    public ResponseEntity<ChallengeVerificationResponse> getLatestVerification(
            @Parameter(description = "챌린지 ID", required = true, example = "1")
            @PathVariable Long challengeId
    ) {
        ChallengeVerificationResponse response = challengeVerificationService.getLatestVerification(challengeId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "맴버별 당일이 포함된 주간 인증 횟수",
            description = "챌린지 참여 멤버들의 당일이 포함된 주(월요일~일요일) 동안의 인증 횟수를 조회합니다."
    )
    @GetMapping("/{challengeId}/member/verification-count")
    public ResponseEntity<List<MembersVerificationCountResponse>>
    getChallengeMemberWeeklyVerificationCounts(@PathVariable Long challengeId) {

        List<MembersVerificationCountResponse> response = challengeVerificationService
                .getChallengeMemberWeeklyVerificationCounts(challengeId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "챌린지의 모든 인증 데이터 조회 (무한 스크롤)",
            description = "해당 챌린지에 참여한 모든 멤버들의 인증 데이터를 최신순으로 페이징하여 조회합니다.\n\n" +
                    "📌 무한 스크롤 방식:\n" +
                    "- Slice를 사용하여 전체 개수 조회 없이 빠른 응답\n" +
                    "- 최신순 정렬 (날짜 DESC, ID DESC)\n" +
                    "- 기본 20개씩 조회\n" +
                    "- hasNext로 다음 페이지 존재 여부 확인\n\n" +
                    "📌 응답 구조:\n" +
                    "- content: 인증 데이터 리스트\n" +
                    "- pageable: 페이징 정보\n" +
                    "- size: 페이지 크기\n" +
                    "- number: 현재 페이지 번호 (0부터 시작)\n" +
                    "- numberOfElements: 현재 페이지의 요소 개수\n" +
                    "- first: 첫 페이지 여부\n" +
                    "- last: 마지막 페이지 여부\n" +
                    "- hasNext: 다음 페이지 존재 여부 ⭐\n\n" +
                    "예: GET /api/challenge-verifications/1/list?page=0&size=20"
    )
    @GetMapping("/{challengeId}/list")
    public ResponseEntity<Slice<ChallengeVerificationResponse>> getAllVerifications(
            @Parameter(description = "챌린지 ID", required = true, example = "1")
            @PathVariable Long challengeId,
            @Parameter(description = "페이지 번호 (0부터 시작)", required = false, example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (기본 20개)", required = false, example = "20")
            @RequestParam(required = false, defaultValue = "5") int size
    ) {
        Slice<ChallengeVerificationResponse> response = challengeVerificationService
                .getAllVerifications(challengeId, page, size);
        return ResponseEntity.ok(response);
    }

}
