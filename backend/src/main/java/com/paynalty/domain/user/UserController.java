package com.paynalty.domain.user;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.paynalty.global.security.CustomUserDetails;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "유저 관련 API")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = UserResponse.from(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @Operation(summary = "유저 목록 검색", description = "키워드를 포함하는 이름과 이메일을 가진 모든 유저 정보를 반환")
    public ResponseEntity<List<UserResponse>> getAllUserByContainString(
            @RequestParam("keyword") String keyword) {
        List<UserResponse> response = userService.findByNameAndEmail(keyword);
        return ResponseEntity.ok(response);
    }
}
