package com.example.steam.module.article.repository;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.article.domain.Article;
import com.example.steam.module.comment.domain.ArticleComment;
import com.example.steam.module.comment.repository.ArticleCommentRepository;
import com.example.steam.module.company.domain.Company;
import com.example.steam.module.company.repository.CompanyRepository;
import com.example.steam.module.gallery.domain.Gallery;
import com.example.steam.module.gallery.repository.GalleryRepository;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ArticleRepositoryTest2 {
    @Autowired private ArticleRepository articleRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private GalleryRepository galleryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ArticleCommentRepository articleCommentRepository;
    @Autowired private CompanyRepository companyRepository;

    @BeforeEach
    void init(){
        List<Member> members = new ArrayList<>();
        List<Company> companies = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<Gallery> galleries = new ArrayList<>();
        for(int i = 1; i <= 5; i++){
            members.add(Member.makeSample(i));
            companies.add(Company.makeSample(i));
            products.add(Product.makeSample(i, companies.get(i-1)));
            galleries.add(Gallery.makeSample(products.get(i-1)));
        }
        memberRepository.saveAll(members);
        companyRepository.saveAll(companies);
        productRepository.saveAll(products);
        galleryRepository.saveAll(galleries);
        for(int i = 0; i < 5; i++){
            Gallery gallery = galleries.get(i);
            List<Article> articles = new ArrayList<>();
            for(int j = 0; j < 5; j++){
                Article article = Article.makeSample(i*10+j, gallery, members.get(j));
                articleRepository.save(article);
                for(int k = 0; k < 5; k++){
                    ArticleComment.makeSample(members.get(k), article, i*100+j*10+k);
                }
            }
        }
    }
    @Test
    @DisplayName("갤러리에 해당하는 개시물 찾기")
    void findAllByGalleryTest(){
        //given
        Gallery gallery = galleryRepository.findAll().get(0);
        Pageable pageable = PageRequest.of(0, PageConst.ARTICLE_PAGE_SIZE);

        //when
        Page<Article> articlePage = articleRepository.findAllByGalleryId(gallery.getId(), pageable);

        //then
        assertThat(articlePage.getTotalElements()).isEqualTo(5);
        assertThat(articlePage.get().allMatch(a -> Objects.equals(a.getGallery().getId(), gallery.getId()))).isTrue();
    }

    @Test
    @DisplayName("갤러리 속 검색어: 제목 찾기")
    void searchInGalleryTest1(){
        //given
        Gallery gallery = galleryRepository.findAll().get(0);
        Pageable pageable = PageRequest.of(0, PageConst.ARTICLE_PAGE_SIZE);
        Article article = articleRepository.findAllByGalleryId(gallery.getId(), pageable).getContent().get(0);
        String searchWord1 = article.getTitle().substring(0, "title11".length()-2);
        String searchWord2 = article.getTitle().substring(0, "title11".length()-1);
        //when
        Page<Article> articlePage1 = articleRepository.findAllByGalleryIdAndTitleContaining(gallery.getId(), searchWord1, pageable);
        Page<Article> articlePage2 = articleRepository.findAllByGalleryIdAndTitleContaining(gallery.getId(), searchWord2, pageable);

        //then
        assertThat(articlePage1.getTotalElements()).isEqualTo(5);
        assertThat(articlePage2.getTotalElements()).isEqualTo(1);
        assertThat(articlePage1.get().allMatch(a -> a.getGallery().getId().equals(gallery.getId()))).isTrue();
        assertThat(articlePage2.get().allMatch(a -> a.getTitle().equals(searchWord2))).isTrue();
    }

    @Test
    @DisplayName("갤러리 속 검색어: 내용 찾기")
    void searchInGalleryTest2(){
        //given
        Gallery gallery = galleryRepository.findAll().get(3);
        Pageable pageable = PageRequest.of(0, PageConst.ARTICLE_PAGE_SIZE);
        Article article = articleRepository.findAllByGalleryId(gallery.getId(), pageable).getContent().get(0);
        String searchWord = article.getContent().substring(0, article.getContent().length()-2);

        //when
        Page<Article> articlePage = articleRepository.findAllByGalleryIdAndContentContaining(gallery.getId(), searchWord, pageable);

        //then
        assertThat(articlePage.get().allMatch(a -> a.getGallery().getId().equals(gallery.getId()))).isTrue();
        assertThat(articlePage.get().allMatch(a -> a.getContent().contains(searchWord))).isTrue();

    }

    @Test
    @DisplayName("갤러리 속 검색어: 멤버 닉네임 찾기")
    void searchInGalleryTest3(){
        //given
        Gallery gallery = galleryRepository.findAll().get(0);
        Pageable pageable = PageRequest.of(0, PageConst.ARTICLE_PAGE_SIZE);
        Article article = articleRepository.findAllByGalleryId(gallery.getId(), pageable).getContent().get(0);
        Member member = article.getMember();
        String searchWord = member.getNickname().substring(0, member.getNickname().length()-2);

        //when
        Page<Article> articlePage = articleRepository.findAllByGalleryIdAndMemberNicknameContaining(gallery.getId(), searchWord, pageable);


        //then
        assertThat(articlePage.get().allMatch(a -> a.getGallery().getId().equals(gallery.getId()))).isTrue();
        assertThat(articlePage.get().allMatch(a -> a.getMember().getNickname().contains(searchWord))).isTrue();
    }

    @Test
    @DisplayName("갤러리 속 검색어: 댓글 내용 찾기")
    void searchInGalleryTest4(){
        //given
        Gallery gallery = galleryRepository.findAll().get(0);
        Pageable pageable = PageRequest.of(0, PageConst.ARTICLE_PAGE_SIZE);
        Article article = articleRepository.findAllByGalleryId(gallery.getId(), pageable).getContent().get(0);
        String commentContent = article.getComments().get(3).getContent();
        String searchWord = commentContent.substring(0, commentContent.length()-2);

        //when
        Page<Article> articlePage = articleRepository.findAllByGalleryIdAndCommentsContentContaining(gallery.getId(), searchWord, pageable);

        //then
        assertThat(articlePage.get().allMatch(a -> a.getGallery().getId().equals(gallery.getId()))).isTrue();
        assertThat(articlePage.get().allMatch(a -> a.getComments().stream().anyMatch(c -> c.getContent().contains(searchWord)))).isTrue();

    }

    @Test
    @DisplayName("갤러리 속 검색어: 전체 찾기")
    void searchInGalleryTest5(){
        //given
        Gallery gallery = galleryRepository.findAll().get(4);
        Pageable pageable = PageRequest.of(0, PageConst.ARTICLE_PAGE_SIZE);
        Article article = articleRepository.findAllByGalleryId(gallery.getId(), pageable).getContent().get(0);
        Member member = article.getMember();
        String titleSearchWord = article.getTitle().substring(0, article.getTitle().length()-2);
        String contentSearchWord = article.getContent().substring(0, article.getContent().length()-2);
        String memberNicknameSearchWord = member.getNickname().substring(0, member.getNickname().length()-2);
        String commentSearchWord = article.getComments().get(3).getContent();

        //when
        Page<Article> titleArticlePage = articleRepository.findAllByGalleryIdAndMemberNameOrContentOrTitleContaining(gallery.getId(), titleSearchWord, pageable);
        Page<Article> contentArticlePage = articleRepository.findAllByGalleryIdAndMemberNameOrContentOrTitleContaining(gallery.getId(), contentSearchWord, pageable);
        Page<Article> memberNicknameArticlePage = articleRepository.findAllByGalleryIdAndMemberNameOrContentOrTitleContaining(gallery.getId(), memberNicknameSearchWord, pageable);
        Page<Article> commentContentArticlePage = articleRepository.findAllByGalleryIdAndMemberNameOrContentOrTitleContaining(gallery.getId(), commentSearchWord, pageable);

        //then
        assertThat(titleArticlePage.get().allMatch(a -> a.getGallery().getId().equals(gallery.getId()))).isTrue();
        assertThat(titleArticlePage.get().allMatch(a -> a.getTitle().contains(titleSearchWord))).isTrue();
        assertThat(contentArticlePage.get().allMatch(a -> a.getGallery().getId().equals(gallery.getId()))).isTrue();
        assertThat(contentArticlePage.get().allMatch(a -> a.getContent().contains(contentSearchWord))).isTrue();
        assertThat(memberNicknameArticlePage.get().allMatch(a -> a.getGallery().getId().equals(gallery.getId()))).isTrue();
        assertThat(memberNicknameArticlePage.get().allMatch(a -> a.getMember().getNickname().contains(memberNicknameSearchWord))).isTrue();
        assertThat(commentContentArticlePage.get().allMatch(a -> a.getGallery().getId().equals(gallery.getId()))).isTrue();
        assertThat(commentContentArticlePage.get().allMatch(a -> a.getComments().stream().anyMatch(c -> c.getContent().contains(commentSearchWord)))).isTrue();
    }
}
