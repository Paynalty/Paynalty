package com.paynalty.domain.toss;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/toss")
public class TossController {

    private final TossService tossService;

    @Tag(name = "Toss Login API", description = "(개발중) 토스 인증 관련 API")
    @GetMapping("/test")
    public String testToss() throws Exception {
        System.out.println(">>> TossController reached");
        return tossService.refreshToken();
    }
}
