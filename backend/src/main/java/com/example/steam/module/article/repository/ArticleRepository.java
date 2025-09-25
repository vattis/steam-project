package com.example.steam.module.article.repository;

import com.example.steam.module.article.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findAllByOrderByCreated(Pageable pageable);

    @Query("""
        select distinct a
        from Article a
        join fetch a.member m
        join a.gallery g
        where g.id = :galleryId
        and a.deleted = false
        and m.deleted = false
        and g.deleted = false
""")
    Page<Article> findAllByGalleryId(Long galleryId, Pageable pageable);
    @EntityGraph(attributePaths = {"member"})

    Page<Article> findAllByGalleryIdAndTitleContaining(Long galleryId, String searchWord, Pageable pageable);
    @EntityGraph(attributePaths = {"member"})

    Page<Article> findAllByGalleryIdAndContentContaining(Long galleryId, String searchWord, Pageable pageable);
    @EntityGraph(attributePaths = {"member"})
    Page<Article> findAllByGalleryIdAndMemberNicknameContaining(Long galleryId, String searchWord, Pageable pageable);
    @Query("""
        select distinct a
        from Article a
        join a.gallery g
        join a.comments c
        where g.id = :galleryId and c.content like concat('%', :searchWord, '%')
        order by a.created desc
""")
    Page<Article> findAllByGalleryIdAndCommentsContentContaining(Long galleryId, String searchWord, Pageable pageable);

    @Query("""
        select distinct a
        from Article a
        join a.gallery g
        join a.member m
        left join a.comments c
        where g.id = :galleryId and 
        (a.title like concat('%', :searchWord, '%') 
        or a.content like concat('%', :searchWord, '%')
        or m.nickname like concat('%', :searchWord, '%')
        or c.content like concat('%', :searchWord, '%'))
        order by a.created desc
""")
    Page<Article> findAllByGalleryIdAndMemberNameOrContentOrTitleContaining(Long galleryId, String searchWord, Pageable pageable);
}
