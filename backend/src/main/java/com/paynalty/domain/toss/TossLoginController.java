package com.paynalty.domain.toss;

import com.paynalty.domain.toss.dto.TossLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/toss")
@RequiredArgsConstructor
public class TossLoginController {

    private final TossApiClient tossApiClient;

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> handleTossLogin(@RequestBody TossLoginRequest loginRequest) {
        return Mono.empty();
    }
}
