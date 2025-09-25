package com.example.steam.module.comment.repository;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.article.domain.Article;
import com.example.steam.module.article.repository.ArticleRepository;
import com.example.steam.module.comment.domain.ArticleComment;
import com.example.steam.module.company.domain.Company;
import com.example.steam.module.company.repository.CompanyRepository;
import com.example.steam.module.gallery.domain.Gallery;
import com.example.steam.module.gallery.repository.GalleryRepository;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class ArticleCommentRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ArticleCommentRepository articleCommentRepository;
    @Autowired
    private GalleryRepository galleryRepository;
    @Autowired
    EntityManager em;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void init(){
        List<Member> members = new ArrayList<>();
        List<Article> articles = new ArrayList<>();
        List<ArticleComment> articleComments = new ArrayList<>();
        Company company = Company.makeSample(1);
        Product product = Product.makeSample(1, company);
        companyRepository.save(company);
        productRepository.save(product);
        Gallery gallery = galleryRepository.save(Gallery.makeSample(product));
        for(int i = 1; i <= 10; i++){
            members.add(Member.makeSample(i));
        }
        for(int i = 1; i <= 10; i++){
            articles.add(Article.makeSample(i, gallery, members.get(i-1)));
        }
        memberRepository.saveAll(members);
        articleRepository.saveAll(articles);

        for(int i = 1; i <= 10; i++){
            ArticleComment articleComment1 = ArticleComment.makeSample(members.get(i-1), articles.get(i-1), i);
            ArticleComment articleComment2 = ArticleComment.makeSample(members.get(i-1), articles.get(i-1), i);
            articleComments.add(articleComment1);
            articleComments.add(articleComment2);
        }
        articleCommentRepository.saveAll(articleComments);
        em.flush();
        em.clear();
    }

    @Test
    void saveAndFindTest(){
        //given
        PageRequest pageRequest = PageRequest.of(0, PageConst.ARTICLE_COMMENT_PAGE_SIZE);
        Article article = articleRepository.findAll().get(0);

        //when
        Page<ArticleComment> articleCommentPage = articleCommentRepository.findAllByArticleId(article.getId(), pageRequest);

        //then
        assertThat(articleCommentPage.getTotalElements()).isEqualTo(2);
        assertThat(articleCommentPage.stream().allMatch(ac -> Objects.equals(ac.getArticle().getId(), article.getId()))).isTrue();
    }


    @Test
    @DisplayName("comment 삭제 테스트")
    void deleteTest1(){
        //given
        Article article = articleRepository.findAll().get(0);
        PageRequest pageRequest = PageRequest.of(0, PageConst.ARTICLE_COMMENT_PAGE_SIZE);
        ArticleComment articleComment = article.getComments().get(0);

        //when
        articleCommentRepository.delete(articleComment);

        //then
        assertThat(articleCommentRepository.findById(articleComment.getId()).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("article를 삭제할 시 comment도 삭제되는지 확인")
    void deleteTest2(){
        //given
        Article article = articleRepository.findAll().get(0);
        PageRequest pageRequest = PageRequest.of(0, PageConst.ARTICLE_COMMENT_PAGE_SIZE);

        //when
        articleRepository.delete(article);

        //then
        assertThat(articleCommentRepository.findAllByArticleId(article.getId(), pageRequest).getTotalElements()).isEqualTo(0);
    }
}