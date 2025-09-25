package com.example.steam.module.gallery.domain;

import com.example.steam.module.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql="UPDATE gallery SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Gallery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Product product;

    @Column(name="deleted", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean deleted = false;

    public static Gallery makeSample(Product product){
        return Gallery.builder()
                .product(product)
                .build();
    }

    public static Gallery of(Product product){
        return Gallery.builder()
                .product(product)
                .build();
    }
}
