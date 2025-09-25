package com.example.steam.module.product.dto;

import com.example.steam.module.comment.dto.ProductCommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Builder
@AllArgsConstructor
@Getter
public class ProductWithCommentDto {
    private DetailProductDto detailProductDto;
    private Page<ProductCommentDto> productCommentDtos;

}
