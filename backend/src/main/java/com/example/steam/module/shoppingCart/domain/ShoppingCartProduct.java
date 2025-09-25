package com.example.steam.module.shoppingCart.domain;

import com.example.steam.module.product.domain.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name="shopping_cart_product",
        uniqueConstraints = @UniqueConstraint(columnNames = {"shopping_cart_id", "product_id"}))
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql="UPDATE shopping_cart_product SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class ShoppingCartProduct {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ShoppingCart shoppingCart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Product product;


    @Column(name="deleted", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean deleted = false;

    public static ShoppingCartProduct of(ShoppingCart shoppingCart, Product product){
        return ShoppingCartProduct.builder()
                .shoppingCart(shoppingCart)
                .product(product)
                .build();
    }
}
