package com.paynalty.global.toss;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TossController {

    private final TossService tossService;

    @GetMapping("/toss/test")
    public String testToss() throws Exception {
        System.out.println(">>> TossController reached");
        return tossService.refreshToken();
    }
}
