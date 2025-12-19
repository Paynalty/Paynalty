package com.paynalty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PaynaltyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaynaltyApplication.class, args);
    }

}
