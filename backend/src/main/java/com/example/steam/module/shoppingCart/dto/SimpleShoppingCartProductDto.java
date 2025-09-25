package com.example.steam.module.shoppingCart.dto;

import com.example.steam.module.product.domain.Product;
import com.example.steam.module.shoppingCart.domain.ShoppingCartProduct;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SimpleShoppingCartProductDto {
    private Long id;
    private String productName;
    private Integer productPrice;
    private String avatarUrl;

    public static SimpleShoppingCartProductDto from(ShoppingCartProduct shoppingCartProduct){
        Product product = shoppingCartProduct.getProduct();
        return SimpleShoppingCartProductDto
                .builder()
                .id(shoppingCartProduct.getId())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .avatarUrl(product.getImageUrl())
                .build();
    }
}
