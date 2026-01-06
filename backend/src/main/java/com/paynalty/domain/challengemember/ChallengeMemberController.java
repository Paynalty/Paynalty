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

    @Operation(
            summary = "챌린지 멤버 목록 조회",
            description = "특정 챌린지에 참여 중인 멤버 목록을 조회합니다."
    )
    @GetMapping("/{challengeId}")
    public ResponseEntity<List<ChallengeMemberResponse>> getMembers(
            @Parameter(description = "챌린지 ID", required = true, example = "1")
            @PathVariable Long challengeId
    ) {
        List<ChallengeMemberResponse> response = challengeMemberService.getMembersByChallengeId(challengeId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "챌린지에 친구 초대 (멤버 추가)",
            description = "챌린지 생성 후 추가로 친구를 초대하여 멤버로 추가합니다.\n\n"
    )
    @PostMapping
    public ResponseEntity<String> addMember(
            @Parameter(description = "챌린지 멤버 초대 요청 정보", required = true)
            @Valid @RequestBody ChallengeMemberRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long tossId = userDetails.getUser().getTossId();
        
        String result = challengeMemberService.addMemberByInvitation(
                request.getChallengeId(),
                tossId,
                request.getInviteeName(),
                request.getInviteePhoneNumber()
        );
        
        return ResponseEntity.ok(result);
    }

}
