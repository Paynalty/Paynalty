package com.paynalty.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUserByContainString(
            @RequestParam("keyword") String keyword) {
        List<UserResponse> response = userService.findByNameAndEmail(keyword);
        return ResponseEntity.ok(response);
    }

}
