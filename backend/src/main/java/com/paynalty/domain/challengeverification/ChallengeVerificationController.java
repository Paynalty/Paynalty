package com.paynalty.domain.challengeverification;

import com.paynalty.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
                    "📎 파일 업로드:\n" +
                    "- multipart/form-data 형식으로 이미지 파일 업로드\n" +
                    "- 지원 형식: jpg, jpeg, png, gif\n\n" +
                    "🔐 JWT 토큰 인증 필수 (Authorization: Bearer {token})"
    )
    @PostMapping(value = "/{challengeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChallengeVerificationResponse> create(
            @Parameter(description = "챌린지 ID", required = true, example = "1")
            @PathVariable Long challengeId,
            @Parameter(description = "인증 이미지 파일", required = true)
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long tossId = userDetails.getUser().getTossId();
        ChallengeVerificationResponse response = challengeVerificationService.create(challengeId, tossId, image);
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
        if (response == null) {
            return ResponseEntity.ok(null);
        }
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

    @Operation(
            summary = "인증 데이터 수정",
            description = "본인의 인증 데이터의 이미지를 수정합니다.\n\n" +
                    "📎 파일 업로드:\n" +
                    "- multipart/form-data 형식으로 이미지 파일 업로드\n" +
                    "- 지원 형식: jpg, jpeg, png, gif\n\n" +
                    "🔐 JWT 토큰 인증 필수 (Authorization: Bearer {token})\n" +
                    "⚠️ 본인의 인증만 수정 가능"
    )
    @PutMapping(value = "/{verificationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChallengeVerificationResponse> update(
            @Parameter(description = "인증 ID", required = true, example = "1")
            @PathVariable Long verificationId,
            @Parameter(description = "수정할 인증 이미지 파일", required = true)
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long tossId = userDetails.getUser().getTossId();
        ChallengeVerificationResponse response = challengeVerificationService.update(verificationId, tossId, image);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "인증 데이터 삭제",
            description = "본인의 인증 데이터를 삭제합니다.\n\n" +
                    "🔐 JWT 토큰 인증 필수 (Authorization: Bearer {token})\n" +
                    "⚠️ 본인의 인증만 삭제 가능"
    )
    @DeleteMapping("/{verificationId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "인증 ID", required = true, example = "1")
            @PathVariable Long verificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long tossId = userDetails.getUser().getTossId();
        challengeVerificationService.delete(verificationId, tossId);
        return ResponseEntity.ok().build();
    }

}
