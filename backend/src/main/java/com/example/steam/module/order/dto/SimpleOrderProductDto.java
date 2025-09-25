package com.example.steam.module.order.dto;

import com.example.steam.module.order.domain.OrderProduct;
import com.example.steam.module.product.dto.SimpleProductDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SimpleOrderProductDto {
    private Long orderProductId;
    private Long orderId;
    private SimpleProductDto simpleProductDto;
    private int count;

    public static SimpleOrderProductDto of(Long orderProductId, Long orderId, SimpleProductDto simpleProductDto) {
        return SimpleOrderProductDto.builder()
                .orderProductId(orderProductId)
                .orderId(orderId)
                .simpleProductDto(simpleProductDto)
                .build();
    }

    public static SimpleOrderProductDto fromEntity(OrderProduct orderProduct){
        return SimpleOrderProductDto.of(orderProduct.getId(), orderProduct.getOrder().getId(), SimpleProductDto.fromEntity(orderProduct.getProduct()));
    }
}
