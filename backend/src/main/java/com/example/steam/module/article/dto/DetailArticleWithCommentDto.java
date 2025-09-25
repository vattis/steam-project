package com.example.steam.module.article.dto;

import com.example.steam.module.comment.dto.ArticleCommentDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Builder
@Getter
public class DetailArticleWithCommentDto {
    private DetailArticleDto articleDto;
    private Page<ArticleCommentDto> commentDtoPage;

    public static DetailArticleWithCommentDto of(DetailArticleDto articleDto, Page<ArticleCommentDto> commentDtoPage){
        return DetailArticleWithCommentDto.builder()
                .articleDto(articleDto)
                .commentDtoPage(commentDtoPage)
                .build();
    }
}
