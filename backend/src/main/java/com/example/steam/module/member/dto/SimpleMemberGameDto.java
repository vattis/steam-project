package com.example.steam.module.member.dto;

import com.example.steam.module.member.domain.MemberGame;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class SimpleMemberGameDto {
    private Long id;
    private Long productId;
    private String productName;

    public static SimpleMemberGameDto from(MemberGame memberGame){
        return SimpleMemberGameDto
                .builder()
                .id(memberGame.getId())
                .productId(memberGame.getProduct().getId())
                .productName(memberGame.getProduct().getName())
                .build();
    }
}
