package com.example.steam.module.comment.dto;


import com.example.steam.module.comment.domain.ProductComment;
import com.example.steam.module.member.dto.SimpleMemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class ProductCommentDto {
    private Long id;
    private SimpleMemberDto member;
    private String content;
    private LocalDateTime createdTime;
    private Float rating;

    public static ProductCommentDto from(ProductComment productComment) {
        return ProductCommentDto.builder()
                .id(productComment.getId())
                .member(SimpleMemberDto.from(productComment.getMember()))
                .content(productComment.getContent())
                .createdTime(productComment.getCreatedTime())
                .rating(productComment.getRate())
                .build();
    }
}
