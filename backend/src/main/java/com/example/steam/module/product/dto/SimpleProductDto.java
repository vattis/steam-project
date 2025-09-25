package com.example.steam.module.product.dto;

import com.example.steam.module.product.domain.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SimpleProductDto {
    private Long id;
    private String name;
    private String companyName;
    private Integer commentCount;
    private Float rate;

    public static SimpleProductDto of(Long id, String name, String companyName, Integer commentCount, Float rate){
        return SimpleProductDto.builder()
                .id(id)
                .name(name)
                .companyName(companyName)
                .commentCount(commentCount)
                .rate(rate)
                .build();
    }

    public static SimpleProductDto fromEntity(Product product){
        return SimpleProductDto.of(product.getId(), product.getName(), product.getCompany().getName(), product.getProductComments().size(), product.getRate());
    }
}
