package com.example.steam.module.discount.dto;

import com.example.steam.module.discount.domain.Discount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class SimpleDiscountDto {
    private Long id;
    private Long productId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer discountRate;
    private boolean active;

    public static SimpleDiscountDto of(Long id, Long productId, LocalDateTime startDate, LocalDateTime endDate, Integer discountRate, boolean active){
        return SimpleDiscountDto.builder()
                .id(id)
                .productId(productId)
                .startDate(startDate)
                .endDate(endDate)
                .discountRate(discountRate)
                .active(active)
                .build();
    }

    public static SimpleDiscountDto from(Discount discount){
        if(discount == null) return null;
        return SimpleDiscountDto.of(discount.getId(), discount.getProduct().getId(), discount.getStartTime(), discount.getEndTime(), discount.getDiscountRate(), discount.getActive());
    }
}
