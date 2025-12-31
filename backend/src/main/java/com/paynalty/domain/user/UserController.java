package com.paynalty.domain.user;

import com.paynalty.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(
            @RequestBody UserRequest request
    ){
        UserResponse response = userService.create(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUserByContainString(
            @RequestParam("keyword") String keyword
    ){
        List<UserResponse> response = userService.findByNameAndEmail(keyword);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
