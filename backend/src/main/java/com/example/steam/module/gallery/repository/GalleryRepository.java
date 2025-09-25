package com.example.steam.module.gallery.repository;

import com.example.steam.module.gallery.domain.Gallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    @Query("""
        select distinct g
        from Gallery g
        join fetch g.product p
        left join fetch p.discount d
        where p.name like concat('%', :searchWord, '%')
        and g.deleted = false
        and p.deleted = false
""")
    //@EntityGraph(attributePaths = {"product", "product.discount"})
    Page<Gallery> findByProduct_NameContaining(String searchWord, Pageable pageable);

    @EntityGraph(attributePaths = {"product", "product.discount"})
    @Query("""
        select distinct g
        from Gallery g
        where g.product in (
        select distinct mg.product
        from MemberGame mg
        where mg.member.id = :memberId
        and mg.deleted = false)
""")
    List<Gallery> findAllByMemberOwnedProduct(Long memberId);

    Optional<Gallery> findByProduct_Name(String productName);

    @EntityGraph(attributePaths = {"product", "product.discount"})
    Page<Gallery> findAll(Pageable pageable);
}
