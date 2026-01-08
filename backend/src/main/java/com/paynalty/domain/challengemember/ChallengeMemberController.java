package com.paynalty.domain.challengemember;

import com.paynalty.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Challenge Member", description = "챌린지 멤버 관리 API")
@RestController
@RequestMapping("/api/challenge-members")
@RequiredArgsConstructor
public class ChallengeMemberController {

        private final ChallengeMemberService challengeMemberService;

        @Operation(summary = "챌린지 멤버 목록 조회", description = "특정 챌린지에 참여 중인 멤버 목록을 조회합니다.")
        @GetMapping("/{challengeId}")
        public ResponseEntity<List<ChallengeMemberResponse>> getMembers(
                        @Parameter(description = "챌린지 ID", required = true, example = "1") @PathVariable Long challengeId) {
                List<ChallengeMemberResponse> response = challengeMemberService.getMembersByChallengeId(challengeId);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "챌린지 멤버 목록 수정(챌린지 CREATOR 전용)", description = "챌린지 멤버 목록 요청된 상태로 업데이트 \n\n" +
                        "동작 방식:\n" +
                        "- 최종 목록 `userTossIds` 목록을 기준으로 멤버를 추가하거나 삭제하여 멤버 목록 업데이트\n" +
                        "JWT 토큰 인증 필수")
        @PutMapping("/{challengeId}/members")
        public ResponseEntity<Void> updateMembers(
                        @Parameter(description = "챌린지 ID", required = true) @PathVariable Long challengeId,
                        @Valid @RequestBody ChallengeMemberUpdateRequest request,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                Long requesterUserId = userDetails.getUser().getId();
                challengeMemberService.updateChallengeMembers(challengeId, request.getUserTossIds(), requesterUserId);

                return ResponseEntity.ok().build();
        }

        @Operation(summary = "챌린지 탈퇴", description = "참여 중인 챌린지에서 탈퇴합니다. (생성자는 탈퇴 불가)")
        @DeleteMapping("/{challengeId}/me")
        public ResponseEntity<Void> withdrawChallenge(
                        @Parameter(description = "챌린지 ID", required = true) @PathVariable Long challengeId,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                Long userId = userDetails.getUser().getId();
                challengeMemberService.withdrawChallenge(challengeId, userId);
                return ResponseEntity.noContent().build();
        }
}
