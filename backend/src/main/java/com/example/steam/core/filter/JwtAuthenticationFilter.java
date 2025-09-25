package com.example.steam.core.filter;

import com.example.steam.core.security.jwt.JwtProvider;
import com.example.steam.module.login.application.LoginService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtProvider jwtProvider;
    private final LoginService loginService;
    private final CacheManager cacheManager;

    //request에서 token을 추출 -> 유효성 검사 -> authentication 변환 -> SecurityContext에 authentication 저장
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();

        if (isExcluded(uri)) { //예외처리 해야하는 uri 설정
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String token = jwtProvider.resolveAccessToken(request);
        if(StringUtils.hasText(token)) {
            if(isBlacklisted(token)){
                log.info("유효하지 않은 토큰: 블랙리스트 됨");
                SecurityContextHolder.clearContext(); //스레드 로컬을 비워줌?
                response.sendRedirect("/login");
            }
            try{
                if(jwtProvider.validateToken(token)){
                    Authentication auth = jwtProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }else{
                    response.sendRedirect("/login");
                    return;
                }
            }catch (ExpiredJwtException e){
                log.info("토큰 만료 : /login 리다이렉트");
                loginService.logout(request, response);
                response.sendRedirect("/login");
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    //request에서 token 추출
    private boolean isExcluded(String uri) {
        return EXCLUDE_PREFIXES.stream().anyMatch(uri::startsWith);
    }

    private static final Set<String> EXCLUDE_PREFIXES = Set.of( //필터를 패스하는 패턴
            "/login",
            "/sign-up",
            "/favicon.ico",
            "/.well-known",
            "/error"
    );

    private boolean isBlacklisted(String token){
        Cache cache = cacheManager.getCache("blacklist::jwt:time");
        if(cache == null) return false;
        return cache.get(token) != null;
    }
}
