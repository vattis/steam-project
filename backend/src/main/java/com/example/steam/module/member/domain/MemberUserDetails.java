package com.example.steam.module.member.domain;

import com.example.steam.module.member.dto.CurrentMemberDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Builder
@Getter
public class MemberUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String password;

    //로그인에 자주 쓰이는 정보들
    private final CurrentMemberDto currentMemberDto;

    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public static MemberUserDetails createUserDetails(Long id, String email, String password, String nickname, Role role){
        List<String> roles = new ArrayList<>();
        roles.add(role.getLabel());
        return MemberUserDetails.builder()
                .id(id)
                .username(email)
                .password(password)
                .currentMemberDto(CurrentMemberDto.of(id, nickname, email))
                .roles(roles)
                .build();
    }
}
