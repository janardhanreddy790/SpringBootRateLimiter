
package com.nagam.example;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class CustomKeyResolver {
    @Bean(name = "customRateKeyResolver")
    public KeyResolver customKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            String path = exchange.getRequest().getPath().toString();
            return Mono.just(ip + ":" + path);
        };
    }
}
