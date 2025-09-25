package com.example.steam.core.config.load_test_config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Profile("load")
@Configuration
public class LoadTestSecurityConfig {
    @Order(1)
    @Bean
    SecurityFilterChain wsPass(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/ws-stomp", "/ws-stomp/**") // ← 정확 매칭 추가
                .authorizeHttpRequests(a -> a.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain permitAll(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(a -> a.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .headers(h -> h.frameOptions(f -> f.disable()))
                .build();
    }
}
