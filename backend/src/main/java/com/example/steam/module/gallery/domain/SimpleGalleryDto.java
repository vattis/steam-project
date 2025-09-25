package com.example.steam.module.gallery.domain;

import com.example.steam.module.member.domain.MemberGame;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SimpleGalleryDto {
    private String galleryName;

    public static SimpleGalleryDto makeDtoWithMemberGame(MemberGame memberGame) {
        return SimpleGalleryDto.builder()
                .galleryName(memberGame.getProduct().getName())
                .build();
    }

    public static SimpleGalleryDto from(Gallery gallery) {
        return SimpleGalleryDto.builder()
                .galleryName(gallery.getProduct().getName())
                .build();
    }
}
