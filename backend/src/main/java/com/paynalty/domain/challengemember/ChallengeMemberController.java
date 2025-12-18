package com.paynalty.domain.challengemember;

import com.paynalty.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenge-members")
@RequiredArgsConstructor
public class ChallengeMemberController {

    private final ChallengeMemberService challengeMemberService;

    /**
     * 연락처에서 선택한 친구들을 챌린지에 추가
     */
    @PostMapping("/from-contacts")
    public ResponseEntity<ApiResponse<List<ChallengeMemberResponse>>> addMembersFromContacts(
            @Valid @RequestBody AddMemberFromContactsRequest request) {
        List<ChallengeMemberResponse> response = challengeMemberService.addMembersFromContacts(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 챌린지의 멤버 목록 조회
     */
    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<ApiResponse<List<ChallengeMemberResponse>>> getChallengeMembers(
            @PathVariable Long challengeId) {
        List<ChallengeMemberResponse> response = challengeMemberService.getChallengeMembers(challengeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

