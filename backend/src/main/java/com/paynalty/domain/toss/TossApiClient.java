package com.paynalty.domain.toss;

import com.paynalty.global.config.TossApiConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class TossApiClient {

    private final WebClient webClient;
    private final TossApiConfig tossApiConfig;

    public TossApiClient(@Qualifier("tossWebClient") WebClient webClient, TossApiConfig tossApiConfig) {
        this.webClient = webClient;
        this.tossApiConfig = tossApiConfig;
    }
}
