package com.example.steam.module.member.application;

import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.domain.MemberUserDetails;
import com.example.steam.module.member.dto.CurrentMemberDto;
import com.example.steam.module.member.dto.MemberAuthenticationDto;
import com.example.steam.module.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("유저 [" + username + "]을 찾지 못했습니다."));
        return MemberUserDetails.createUserDetails(member.getId(), member.getEmail(), passwordEncoder.encode(member.getPassword()), member.getNickname(), member.getRole());
    }

    @Cacheable(cacheNames = "login-member::jwt:authDto",
            key = "T(org.springframework.util.DigestUtils).md5DigestAsHex(#accessToken.bytes)",
            unless = "#result == null")
    public MemberAuthenticationDto loadMemberAuthenticationDtoByJwt(String accessToken, String username){
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("유저 [" + username + "]을 찾지 못했습니다."));
        return MemberAuthenticationDto.from(member);
    }
}
