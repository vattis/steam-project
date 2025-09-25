package com.example.steam.module.member.dto;

import com.example.steam.module.member.domain.MemberGame;
import com.example.steam.module.product.domain.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberGameDto {
    private Long id;
    private Long gameId;
    private String gameName;
    private int playTime;
    private LocalDateTime lastPlayTime;
    private String gameImageUrl;

    public static MemberGameDto of(Long id, Long gameId, String gameName, int playTime, LocalDateTime lastPlayTime, String gameImageUrl){
        return MemberGameDto.builder()
                .id(id)
                .gameId(gameId)
                .gameName(gameName)
                .playTime(playTime)
                .lastPlayTime(lastPlayTime)
                .gameImageUrl(gameImageUrl)
                .build();
    }
    public static MemberGameDto from(MemberGame memberGame){
        Product product = memberGame.getProduct();
        return MemberGameDto.of(memberGame.getId(), memberGame.getProduct().getId(), product.getName(), memberGame.getPlayMinutes(), memberGame.getLastPlayedTime(), product.getImageUrl());
    }
}
