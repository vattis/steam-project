package com.example.steam.module.comment.repository;

import com.example.steam.module.comment.domain.ProductComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductCommentRepository extends JpaRepository<ProductComment, Long> {
    @Query("""
        select pc
        from ProductComment pc
        join fetch pc.product p
        join fetch pc.member m
        where p.id = :productId
        and pc.deleted = false
        and p.deleted = false
        order by pc.createdTime DESC
""")
    Page<ProductComment> findAllByProductId(Long productId, PageRequest pageRequest);

    void deleteById(Long id);
}
