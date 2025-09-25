package com.example.steam.module.login.application;

import com.example.steam.core.security.jwt.JwtProvider;
import com.example.steam.core.security.jwt.JwtToken;
import com.example.steam.module.login.dto.LoginForm;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class LoginService {
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final CacheManager cacheManager;

    public JwtToken login(LoginForm loginForm) {
        //검증되지 않은 AuthenticationToken
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getPassword());

        Authentication authentication = makeAuthentication(authenticationToken);
        return jwtProvider.generateToken(authentication);

        //JwtToken 생성
    }


    public void logout(HttpServletRequest request, HttpServletResponse response){
        try{
            String accessToken = jwtProvider.resolveAccessToken(request);
            String refreshToken = jwtProvider.resolveRefreshToken(request);
            long accessExpire = jwtProvider.parseClaim(accessToken).getExpiration().getTime();
            long refreshExpire = jwtProvider.parseClaim(refreshToken).getExpiration().getTime();
            addBlacklist(accessToken, accessExpire);
            addBlacklist(refreshToken, refreshExpire);
            evict("login-member::email:memberDto", jwtProvider.parseClaim(accessToken).getSubject());
        }catch (JwtException | IllegalArgumentException e){
            log.warn("토큰 로그아웃 블랙리스트 등록 실패");
        }
        ResponseCookie deleteCookie1 = ResponseCookie.from("accessToken", "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();
        ResponseCookie deleteCookie2 = ResponseCookie.from("refreshToken", "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie1.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie2.toString());
        SecurityContextHolder.clearContext();
    }

    private Authentication makeAuthentication(UsernamePasswordAuthenticationToken authenticationToken) {
        /*
            AuthenticationManager가 authenticate() 메서드를 실행하면 검증 진행
            UserDetailsService를 구현한 CustomUserDetailsService의 loadUserByUsername()메서드를 찾아서 내부에서 실행
            DB로부터 해당되는 Member를 찾아와서 검증된 Authentication으로 반환
        */
        try {
            Authentication authentication = authenticationManagerBuilder.getObject()
                    .authenticate(authenticationToken);
            log.info("로그인 성공: {}", authentication.getName());
            return authentication;
        } catch (UsernameNotFoundException e) {
            log.warn("사용자 정보를 찾을 수 없습니다: {}", e.getMessage());
            throw e; // 또는 사용자에게 친절한 예외 변환
        } catch (BadCredentialsException e) {
            log.warn("비밀번호가 일치하지 않습니다: {}", e.getMessage());
            throw e;
        } catch (AuthenticationException e) {
            log.warn("인증 실패: {}", e.getMessage());
            throw e;
        }
    }

    private void evict(String cacheName, String email){
        String emailKeyMd5 = org.springframework.util.DigestUtils
                .md5DigestAsHex(email.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        Cache cache = cacheManager.getCache(cacheName);
        if(cache != null){
            cache.evictIfPresent(emailKeyMd5);
        }
    }

    @CachePut(cacheNames = "blacklist::jwt:time",
            key = "#token", unless = "#result == null")
    public Long addBlacklist(String token, Long expire){
        return expire;
    }
}
