package com.example.steam.core.config;

import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.domain.MemberUserDetails;
import com.example.steam.module.member.dto.CurrentMemberDto;
import com.example.steam.module.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.NoSuchElementException;

@Component
@ControllerAdvice //뷰를 반환하는 controller 에 한정하여 공통된 동작을 할 수 있게해줌
@RequiredArgsConstructor
public class GlobalModelAttributeAdvice {
    private final MemberRepository memberRepository;

    @ModelAttribute("isLogin")
    public boolean nickName(Authentication authentication){
        return authentication != null && authentication.isAuthenticated();
    }

    @ModelAttribute("currentUser")
    public CurrentMemberDto currentMemberDto(@AuthenticationPrincipal MemberUserDetails memberUserDetails){
        if(memberUserDetails != null){ //로그인이 된 상태일 때
            return memberUserDetails.getCurrentMemberDto();
        }
        return null; //로그인 되지 않은 상태
    }
}
