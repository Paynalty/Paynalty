package com.paynalty.global.toss;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TossController {

    private final TossService tossService;

    public TossController(TossService tossService) {
        this.tossService = tossService;
    }

    @GetMapping("/toss/test")
    public String testToss() throws Exception {
        System.out.println(">>> TossController reached");
        return tossService.callTossApi();
    }
}
