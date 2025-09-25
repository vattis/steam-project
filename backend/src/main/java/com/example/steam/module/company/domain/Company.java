package com.example.steam.module.company.domain;

import com.example.steam.module.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@SQLDelete(sql="UPDATE company SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy="company")
    private List<Product> products = new ArrayList<>();

    @Column(name="deleted", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean deleted = false;

    public void addProduct(Product product) {
        products.add(product);
    }

    public static Company of(String name){
        return Company.builder()
                .name(name)
                .build();
    }

    public static Company makeSample(int num){
        return Company.of("companyName"+num);
    }
}
