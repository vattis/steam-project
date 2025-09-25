package com.example.steam.module.comment.repository;

import com.example.steam.module.comment.domain.ArticleComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    @EntityGraph(attributePaths = {"member"})
    Page<ArticleComment> findAllByArticleId(Long articleId, PageRequest pageRequest);

    void deleteById(Long articleCommentId);
}
