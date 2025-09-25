package com.example.steam.module.comment.dto;

import com.example.steam.module.comment.domain.ArticleComment;
import com.example.steam.module.member.dto.SimpleMemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCommentDto {
    private Long id;
    private Long articleId;
    private String content;
    private SimpleMemberDto memberDto;
    private LocalDateTime createdTime;

    public static ArticleCommentDto from(ArticleComment articleComment){
        return ArticleCommentDto.builder()
                .id(articleComment.getId())
                .articleId(articleComment.getArticle().getId())
                .content(articleComment.getContent())
                .memberDto(SimpleMemberDto.from(articleComment.getMember()))
                .createdTime(articleComment.getCreatedTime())
                .build();
    }
}
