package com.example.steam.module.discount.domain;

import com.example.steam.module.product.domain.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql="UPDATE discount SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name= "product_id")
    private Product product;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column
    private Integer discountRate;

    @Column(nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean active = false;

    @Column(name="deleted", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean deleted = false;

    public static Discount of(Product product, LocalDateTime startTime, LocalDateTime endTime, int discountRate, int discountPrice){
        Discount discount = Discount.builder()
                .startTime(startTime)
                .endTime(endTime)
                .discountRate(discountRate)
                .build();
        product.assignDiscount(discount);
        return discount;
    }

    public static Discount makeSample(int num, Product product){
        Random random = new Random();
        return Discount.of(product, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(num), random.nextInt(100), num);
    }
    public boolean isValid(){
        LocalDateTime now = LocalDateTime.now();
        return startTime.isBefore(now) && endTime.isAfter(now);
    }
    public boolean activeDiscount(){
        if(isValid()){
            active = true;
            return true;
        }
        return false;
    }
    public void assignProduct(Product product){
        this.product = product;
    }
}
