package com.paynalty.domain.user;

import lombok.RequiredArgsConstructor;
import com.paynalty.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "유저 관련 API")
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    @Operation(summary = "유저 목록 검색", description = "키워드를 포함하는 이름과 이메일을 가진 모든 유저 정보를 반환")
    public ResponseEntity<List<UserResponse>> getAllUserByContainString(
            @RequestParam("keyword") String keyword) {
        List<UserResponse> response = userService.findByNameAndEmail(keyword);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unlink")
    @Operation(summary = "유저 연결 해제", description = "토스 연결 해제 후 유저 정보 삭제")
    public ResponseEntity<Void> unlinkUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.unlinkUser(userDetails.getUser().getId());
        return ResponseEntity.ok().build();
    }

}
