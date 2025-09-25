package com.example.steam.module.product.repository;

import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.dto.SimpleProductBannerDto;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
       SELECT new com.example.steam.module.product.dto.SimpleProductBannerDto(
        p.id,
        p.name,
        p.price,
        p.imageUrl,
        d.id,
        d.startTime,
        d.endTime,
        d.discountRate,
        d.active)
        FROM Product p
        LEFT JOIN p.discount d
        ORDER BY p.downloadNum DESC
    """)
    Page<SimpleProductBannerDto> findAllByOrderByDownloadNum(Pageable pageable);

    @EntityGraph(attributePaths = {"discount"})
    Page<Product> findAllByNameContaining(String searchWord, Pageable pageable);

    @EntityGraph(attributePaths = {"discount"})
    Page<Product> findAllByCompanyNameContaining(String searchWord, Pageable pageable);

    @Query(value = "select distinct p from Product p join p.company c where p.name like concat('%', :searchWord, '%') or c.name like concat('%', :searchWord, '%')")
    Page<Product> findAllByNameOrCompanyNameContaining(@Param("searchWord") String searchWord, Pageable pageable);

    @Query("""
        SELECT new com.example.steam.module.product.dto.SimpleProductBannerDto(
        p.id,
        p.name,
        p.price,
        p.imageUrl,
        d.id,
        d.startTime,
        d.endTime,
        d.discountRate,
        d.active)
        FROM Product p
        JOIN  p.discount d
        WHERE d.active = true
        ORDER BY d.discountRate DESC
        """)
    Page<SimpleProductBannerDto> findDiscountProductBanner(Pageable pageable);


    Page<Product> findAllByCompanyId(Long companyId, Pageable pageable);


    @EntityGraph(value = "Product.withDiscount", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Product> findById(Long id);
}
