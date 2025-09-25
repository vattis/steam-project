package com.example.steam.module.article.dto;

import com.example.steam.module.member.dto.SimpleMemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class ArticleWriteForm {
    private String galleryName;
    private String title;
    private String content;
    private LocalDateTime createdDate;

    public static ArticleWriteForm of(String galleryName){
        return ArticleWriteForm.builder()
                .galleryName(galleryName)
                .build();
    }

    public static ArticleWriteForm of(String galleryName, String title, String content) {
        return ArticleWriteForm.builder()
                .galleryName(galleryName)
                .title(title)
                .content(content)
                .build();
    }
}
