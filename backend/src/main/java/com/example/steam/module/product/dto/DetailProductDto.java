package com.example.steam.module.product.dto;

import com.example.steam.module.discount.dto.SimpleDiscountDto;
import com.example.steam.module.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@Getter
@AllArgsConstructor
public class DetailProductDto {
    private Long id;
    private String name;
    private String companyName;
    private Long companyId;
    private SimpleDiscountDto discountDto;
    private String imageUrl;
    private Float rate;
    private int price;

    public static DetailProductDto from(Product product) {
        return DetailProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .rate(product.getRate())
                .companyName(product.getCompany().getName())
                .companyId(product.getCompany().getId())
                .discountDto(SimpleDiscountDto.from(product.getDiscount()))
                .imageUrl(product.getImageUrl())
                .build();
    }

    public int getDiscountPrice() {
        return price*((100-this.getDiscountDto().getDiscountRate()))/100;
    }

}
