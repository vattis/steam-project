package com.example.steam.core.utils;

import com.example.steam.module.member.domain.MemberUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

@Slf4j
public class SecurityUtil {
    public static Long getCurrentUserId() {
        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof MemberUserDetails) {
            return ((MemberUserDetails) principal).getId();
        }
        return null;
    }
    public static Boolean isCurrentUserValidate(Long memberId){
        Long loginUserId = getCurrentUserId();
        if(!Objects.equals(loginUserId, memberId)){
            log.info("부적절한 유저의 접근 확인) 목표 유저: memberId={}     실제 유저={} ", memberId, loginUserId);
            return false;
        }
        return true;
    }
}
