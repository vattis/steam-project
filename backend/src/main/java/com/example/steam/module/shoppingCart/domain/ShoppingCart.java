package com.example.steam.module.shoppingCart.domain;

import com.example.steam.module.member.domain.Member;
import com.example.steam.module.order.domain.OrderProduct;
import com.example.steam.module.order.domain.Orders;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="shopping_cart")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql="UPDATE shopping_cart SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(mappedBy = "shoppingCart")
    //@JoinColumn(nullable = false)
    private Member member;

    @Column(nullable = false)
    private int totalPrice;

    @OneToMany(mappedBy="shoppingCart", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ShoppingCartProduct> shoppingCartProducts = new ArrayList<>();

    @Column(name="deleted", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean deleted = false;

    public static ShoppingCart makeSample(int i, Member member){
        return ShoppingCart.builder()
                .member(member)
                .totalPrice(0)
                .build();
    }
    public void setMember(Member member){
        if(this.member == null){
            this.member = member;
            member.setShoppingCart(this);
        }
    }

    public void addShoppingCartProduct(ShoppingCartProduct shoppingCartProduct){
        this.shoppingCartProducts.add(shoppingCartProduct);
        calculateTotalPrice();
    }

    public static ShoppingCart of(Member member){
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .member(member)
                .totalPrice(0)
                .build();
        return shoppingCart;
    }

    public void calculateTotalPrice(){
        this.totalPrice = 0;
        for(ShoppingCartProduct shoppingCartProduct : shoppingCartProducts){
            if(shoppingCartProduct.getProduct().getPrice() != null){
                this.totalPrice += shoppingCartProduct.getProduct().getPrice();
            }
        }

    }
    public void removeShoppingCartProduct(ShoppingCartProduct shoppingCartProduct){
        shoppingCartProducts.remove(shoppingCartProduct);
    }

    public Orders toOrders(){
        Orders order = Orders.of(member);
        for(ShoppingCartProduct shoppingCartProduct : shoppingCartProducts){
            order.addOrderProduct(OrderProduct.of(order, shoppingCartProduct.getProduct()));
        }
        shoppingCartProducts.clear();
        return order;
    }
}
