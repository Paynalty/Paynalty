package com.paynalty.domain.toss;

import com.paynalty.domain.toss.dto.TossLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/toss")
@RequiredArgsConstructor
public class TossLoginController {

    private final TossApiClient tossApiClient;

    @PostMapping("/login")
    public ResponseEntity<String> handleTossLogin(@RequestBody TossLoginRequest loginRequest) {
        try {
            String tossApiResponse = tossApiClient.fetchToken(
                    loginRequest.getAuthorizationCode(),
                    loginRequest.getReferrer()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<>(tossApiResponse, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error while fetching Toss token.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"An error occurred while communicating with Toss API.\"}");
        }
    }
}
