package com.example.steam.module.article.repository;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.article.domain.Article;
import com.example.steam.module.company.domain.Company;
import com.example.steam.module.company.repository.CompanyRepository;
import com.example.steam.module.gallery.domain.Gallery;
import com.example.steam.module.gallery.repository.GalleryRepository;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ArticleRepositoryTest {
    @Autowired private MemberRepository memberRepository;
    @Autowired private ArticleRepository articleRepository;
    @Autowired private GalleryRepository galleryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired EntityManager entityManager;

    @Test
    void saveTest(){
        //given
        Member member = Member.makeSample(1);
        Company company = Company.makeSample(1);
        Product product = Product.makeSample(1, company);
        Gallery gallery = galleryRepository.save(Gallery.makeSample(product));
        Article article = Article.makeSample(1, gallery, member);
        memberRepository.save(member);

        //when
        companyRepository.save(company);
        productRepository.save(product);
        galleryRepository.save(gallery);
        Article articleResult = articleRepository.save(article);

        //then
        assertThat(article.getId()).isNotNull();
        assertThat(article.getTitle()).isEqualTo(articleResult.getTitle());
        assertThat(article.getContent()).isEqualTo(articleResult.getContent());
        assertThat(article.getLikes()).isEqualTo(articleResult.getLikes());
    }

    @Test
    void findTest(){
        //given
        Member member = Member.makeSample(1);
        Company company = Company.makeSample(1);
        Product product = Product.makeSample(1, company);
        Gallery gallery = Gallery.makeSample(product);
        Article article = Article.makeSample(1, galleryRepository.save(Gallery.makeSample(product)), member);
        memberRepository.save(member);
        companyRepository.save(company);
        productRepository.save(product);
        galleryRepository.save(gallery);
        articleRepository.save(article);

        //when
        Article articleResult = articleRepository.findById(article.getId()).orElseThrow();

        //then
        assertThat(articleResult.getId()).isNotNull();
        assertThat(articleResult.getTitle()).isEqualTo(article.getTitle());
        assertThat(articleResult.getContent()).isEqualTo(article.getContent());
        assertThat(articleResult.getLikes()).isEqualTo(article.getLikes());
    }
    @Test
    void deleteTest1(){
        //given
        Member member = Member.makeSample(1);
        Company company = Company.makeSample(1);
        Product product = Product.makeSample(1, company);
        Gallery gallery = Gallery.makeSample(product);
        Article article = Article.makeSample(1, galleryRepository.save(gallery), member);
        memberRepository.save(member);
        articleRepository.save(article);
        companyRepository.save(company);
        productRepository.save(product);
        galleryRepository.save(gallery);
        entityManager.flush();
        entityManager.clear();

        //when
        articleRepository.delete(article);

        //then
        assertThat(articleRepository.existsById(article.getId())).isFalse();

    }

    @Test
    void deleteTest2(){
        //given
        Member member = Member.makeSample(1);
        Company company = Company.makeSample(1);
        Product product = Product.makeSample(1, company);
        Gallery gallery = Gallery.makeSample(product);
        Article article = Article.makeSample(1, galleryRepository.save(gallery), member);
        memberRepository.save(member);
        companyRepository.save(company);
        productRepository.save(product);
        galleryRepository.save(gallery);
        Article article1 = articleRepository.save(article);
        entityManager.flush();
        entityManager.clear();

        //when
        memberRepository.delete(member);

        //then
        assertThat(articleRepository.existsById(article1.getId())).isFalse();

    }
    @Test
    void findAllOrderByCreatedAtDescTest(){
        //given
        Company company = Company.makeSample(1);
        Product product = Product.makeSample(1, company);
        Pageable pageable = PageRequest.of(0, PageConst.ARTICLE_PAGE_SIZE);
        List<Member> members = new ArrayList<>();
        for(int i = 1; i <= 10; i++){
            members.add(Member.makeSample(i));
        }
        memberRepository.saveAll(members);
        companyRepository.save(company);
        productRepository.save(product);
        List<Article> articles = new ArrayList<>();
        Gallery gallery = galleryRepository.save(Gallery.makeSample(product));
        articles.add(Article.makeSample(111111, gallery, members.get(0)));
        articleRepository.save(articles.get(0));
        for(int i = 0; i < 10; i++){
                Member member = members.get(i);
            for(int j = 1; j <= 10; j++){
                articles.add(Article.makeSample(i*10+j, gallery, member));
            }
        }
        articleRepository.saveAll(articles);
        //when
        Page<Article> articlePage = articleRepository.findAllByOrderByCreated(pageable);
        //then
        assertThat(articlePage.getTotalPages()).isEqualTo(6);
        assertThat(articlePage.getTotalElements()).isEqualTo(101);
        assertThat(articlePage.getNumber()).isEqualTo(0);
    }
}