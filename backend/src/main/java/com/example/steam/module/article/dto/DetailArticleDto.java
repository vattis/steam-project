package com.example.steam.module.article.dto;

import com.example.steam.module.article.domain.Article;
import com.example.steam.module.member.dto.SimpleMemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailArticleDto {
    private Long id;
    private String title;
    private String content;
    private SimpleMemberDto memberDto;
    private int likes;
    private LocalDateTime created;

    public static DetailArticleDto from(Article article){
        return DetailArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .memberDto(SimpleMemberDto.from(article.getMember()))
                .likes(article.getLikes())
                .created(article.getCreated())
                .build();
    }
}
