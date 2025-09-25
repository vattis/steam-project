package com.example.steam.module.article.domain;

import com.example.steam.module.comment.domain.ArticleComment;
import com.example.steam.module.gallery.domain.Gallery;
import com.example.steam.module.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql="UPDATE article SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Gallery gallery;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition="TEXT")
    private String content;

    @OneToMany(mappedBy="article", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ArticleComment> comments = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private int likes = 0;

    @Column(name="deleted", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean deleted = false;

    @Column(nullable = false)
    private LocalDateTime created;

    public static Article of(Gallery gallery, Member member, String title, String content) {
        return Article.builder()
                .gallery(gallery)
                .member(member)
                .title(title)
                .content(content)
                .created(LocalDateTime.now())
                .build();
    }

    public static Article makeSampleWithId(int i, Member member){
        Article article = Article.builder()
                                .id((long)i)
                                .member(member)
                                .title("title"+i)
                                .content("content"+i)
                                .created(LocalDateTime.now())
                                .build();
        member.getArticles().add(article);
        return article;
    }
    public static Article makeSample(int i, Gallery gallery, Member member){
        Article article = Article.builder()
                                .gallery(gallery)
                                .member(member)
                                .title("title"+i)
                                .content("content"+i)
                                .created(LocalDateTime.now())
                                .build();
        member.getArticles().add(article);
        return article;
    }
    public void addComment(ArticleComment articleComment){
        this.comments.add(articleComment);
    }

}
