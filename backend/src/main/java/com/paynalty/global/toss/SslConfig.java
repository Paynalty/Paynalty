package com.paynalty.global.toss;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;

@Configuration
public class SslConfig {

    @Bean
    public SSLContext tossSslContext() throws Exception {
        return TLSClient.createSSLContext(
                "/Users/yurakim/dev/Paynalty-Certs/exp-yura_public.crt",
                "/Users/yurakim/dev/Paynalty-Certs/exp-yura_private.key"
        );
    }

}
