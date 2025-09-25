package com.example.steam.module.member.dto;

import com.example.steam.module.friendship.dto.SimpleFriendshipDto;
import com.example.steam.module.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CurrentMemberDto {
    private Long id;
    private String nickName;
    private String email;

    public static CurrentMemberDto of(Long id, String nickName, String email){
        return CurrentMemberDto.builder()
                .id(id)
                .nickName(nickName)
                .email(email)
                .build();
    }
    public static CurrentMemberDto from(Member member){
        return CurrentMemberDto.of(member.getId(), member.getNickname(), member.getEmail());
    }
}
