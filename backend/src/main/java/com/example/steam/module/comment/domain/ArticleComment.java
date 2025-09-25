package com.example.steam.module.comment.domain;

import com.example.steam.module.article.domain.Article;
import com.example.steam.module.member.domain.Member;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

//커뮤니티 게시글의 댓글
@Entity
@Table(name="article_comment")
@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@OnDelete(action = OnDeleteAction.CASCADE)
public class ArticleComment extends Comment{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Article article;

    public static ArticleComment of(Member member, Article article, String content){
        ArticleComment articleComment = ArticleComment.builder()
                .member(member)
                .content(content)
                .createdTime(LocalDateTime.now())
                .article(article)
                .build();
        article.addComment(articleComment);
        return articleComment;
    }
    public static ArticleComment makeSample(Member member, Article article, int i){
        return ArticleComment.of(member, article, "content"+i);
    }
}
