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

    @Operation(
            summary = "챌린지에 친구 초대 (멤버 추가)",
            description = "챌린지 생성 후 추가로 친구를 초대하여 멤버로 추가합니다.\n\n" +
                    "1. 이름과 전화번호로 User 테이블에서 사용자 검색\n" +
                    "2. 사용자가 존재하면 챌린지 멤버로 추가\n" +
                    "3. 이미 멤버인 경우 중복 추가 방지\n\n" +
                    "4. 테스트용 데이터 Name:테스트유저6 , 전화번호:010-0000-0006"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<String>> addMember(
            @Parameter(description = "삭제 또는 수정 예정 ( 챌린지 맴버 초대)", required = true)
            @Valid @RequestBody ChallengeMemberRequest request
    ) {
        // TODO: 로그인 기능 구현 후 @AuthenticationPrincipal로 현재 사용자 정보 가져오기
        // 초대하는 사람이 챌린지에 참여중인 맴버인지(맴버 중 관리자 역할인지)
        
        String result = challengeMemberService.addMemberByInvitation(
                request.getChallengeId(),
                request.getInviteeName(),
                request.getInviteePhoneNumber()
        );
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

}
