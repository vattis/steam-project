package com.example.steam.module.member.dto;

import com.example.steam.module.friendship.domain.FriendshipState;
import com.example.steam.module.member.domain.Member;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Jackson용
@Getter
public class SimpleMemberDto {
    private Long id;
    private String nickname;
    private String avatarUrl;
    private FriendshipState friendshipState = null;

    public static SimpleMemberDto from(Member member){
        return SimpleMemberDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .build();
    }
}
