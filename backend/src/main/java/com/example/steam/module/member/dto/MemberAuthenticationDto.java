package com.example.steam.module.member.dto;

import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberAuthenticationDto {
    private Long id;
    private String email;
    private String nickname;
    private Role role;

    public static MemberAuthenticationDto of(Long id, String email, String nickname, Role role) {
        return MemberAuthenticationDto.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .role(role)
                .build();
    }

    public static MemberAuthenticationDto from(Member member) {
        return MemberAuthenticationDto.of(member.getId(), member.getEmail(), member.getNickname(), member.getRole());
    }
}
