package com.paynalty.domain.challengemember;

import com.paynalty.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Challenge Member", description = "챌린지 멤버 관리 API")
@RestController
@RequestMapping("/api/challenge-members")
@RequiredArgsConstructor
public class ChallengeMemberController {

    private final ChallengeMemberService challengeMemberService;

    @Operation(
            summary = "챌린지 멤버 목록 조회",
            description = "특정 챌린지에 참여 중인 멤버 목록을 조회합니다."
    )
    @GetMapping("/{challengeId}")
    public ResponseEntity<ApiResponse<List<ChallengeMemberResponse>>> getMembers(
            @Parameter(description = "챌린지 ID", required = true, example = "1")
            @PathVariable Long challengeId
    ) {
        List<ChallengeMemberResponse> response = challengeMemberService.getMembersByChallengeId(challengeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
