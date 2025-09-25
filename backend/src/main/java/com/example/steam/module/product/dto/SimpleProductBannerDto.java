package com.example.steam.module.product.dto;

import com.example.steam.module.discount.domain.Discount;
import com.example.steam.module.discount.dto.SimpleDiscountDto;
import com.example.steam.module.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
public class SimpleProductBannerDto {
    private Long id;
    private String name;
    private Integer price;
    private String imageUrl;
    private Long discountId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer discountRate;
    private Boolean active;

    public SimpleProductBannerDto(Long id, String name, Integer price, String imageUrl, Long discountId, LocalDateTime startTime, LocalDateTime endTime, Integer discountRate, Boolean active) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.discountId = discountId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.discountRate = discountRate;
        this.active = active;
    }

    public static SimpleProductBannerDto of(Long id, String name, Integer price, String imageUrl, Long discountId, LocalDateTime startTime, LocalDateTime endTime, Integer discountRate, Boolean active) {
        return SimpleProductBannerDto.builder()
                .id(id)
                .name(name)
                .price(price)
                .imageUrl(imageUrl)
                .discountId(discountId)
                .startTime(startTime)
                .endTime(endTime)
                .discountRate(discountRate)
                .active(active)
                .build();
    }
    public static SimpleProductBannerDto from(Product product){
        Discount discount = product.getDiscount();
        if(discount == null){
            return SimpleProductBannerDto.of(product.getId(), product.getName(), product.getPrice(), product.getImageUrl(), null, null, null, null, null);
        }
        return SimpleProductBannerDto.of(product.getId(), product.getName(), product.getPrice(), product.getImageUrl(), discount.getId(), discount.getStartTime(), discount.getEndTime(), discount.getDiscountRate(), discount.getActive());
    }
    public int getDiscountPrice() {
        return price*((100-this.discountRate))/100;
    }
}
