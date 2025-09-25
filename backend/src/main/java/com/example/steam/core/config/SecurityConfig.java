package com.example.steam.core.config;

import com.example.steam.core.filter.JwtAuthenticationFilter;
import com.example.steam.core.security.jwt.JwtProvider;
import com.example.steam.module.login.application.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Profile("!load")
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final LoginService loginService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtProvider jwtProvider, CacheManager cacheManager) throws Exception {
        return httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable) //기본 인증 로그인 비활성화
                .csrf(AbstractHttpConfigurer::disable) //서버에 인증정보를 보관하지 않기 때문에 csrf 보호 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer //세션 사용 안함
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests((authorizeRequest)->authorizeRequest
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/static/**", "/login", "/sign-up", "/sign-in", "/shop/**", "/product/**", "/articles", "/auth/**", "/favicon.ico", "/error", "/.well-known/**", "/actuator/**").permitAll()
                        .requestMatchers("/login/test").hasRole("USER")
                        .requestMatchers("/library/**", "/logout").authenticated()
                        .anyRequest().authenticated())
                .exceptionHandling(a->a.accessDeniedPage("/noAuthorities"))
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, loginService, cacheManager), UsernamePasswordAuthenticationFilter.class).build();
    }
}
