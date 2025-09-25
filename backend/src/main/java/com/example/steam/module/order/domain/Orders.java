package com.example.steam.module.order.domain;

import com.example.steam.module.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.query.Order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql="UPDATE orders SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @OneToMany(mappedBy="order", cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @Column(name="deleted", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean deleted = false;

    @Column(nullable = false)
    private int totalPrice;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    public void calcTotalPrice(){
        totalPrice = 0;
        for(OrderProduct orderProduct : orderProducts){
            totalPrice += orderProduct.getProduct().getPrice();
        }
    }

    public static Orders of(Member member){
        Orders order = Orders.builder()
                .member(member)
                .createdDate(LocalDateTime.now())
                .build();
        member.getOrders().add(order);
        return order;
    }

    public void addOrderProduct(OrderProduct orderProduct){
        this.orderProducts.add(orderProduct);
        calcTotalPrice();
    }

    public void removeOrderProduct(OrderProduct orderProduct){
        orderProducts.remove(orderProduct);
        calcTotalPrice();
    }
}
