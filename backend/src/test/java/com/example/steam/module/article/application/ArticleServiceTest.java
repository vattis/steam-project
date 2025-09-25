package com.example.steam.module.article.application;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.article.domain.Article;
import com.example.steam.module.article.domain.ArticleSearch;
import com.example.steam.module.article.domain.ArticleSearchTag;
import com.example.steam.module.article.dto.ArticleWriteForm;
import com.example.steam.module.article.repository.ArticleRepository;
import com.example.steam.module.company.domain.Company;
import com.example.steam.module.gallery.domain.Gallery;
import com.example.steam.module.gallery.repository.GalleryRepository;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.product.domain.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    @InjectMocks ArticleService articleService;
    @Mock ArticleRepository articleRepository;
    @Mock MemberRepository memberRepository;
    @Mock GalleryRepository galleryRepository;

    @Test
    void findAllPageTest(){
        //given
        int pageNo = 0;
        Pageable pageable = PageRequest.of(pageNo, PageConst.ARTICLE_PAGE_SIZE);
        given(articleRepository.findAllByOrderByCreated(pageable)).willReturn(new PageImpl<>(new ArrayList<>(), pageable, 10));

        //when
        Page<Article> articlePage = articleService.findAllPage(pageNo);

        //then
        assertThat(articlePage.getPageable().getPageNumber()).isEqualTo(pageNo);
        verify(articleRepository).findAllByOrderByCreated(pageable);
    }

    @Test
    void findAllByGalleryIdTest(){
        //given
        Long galleryId = 1L;
        int pageNo = 0;
        Pageable pageable = PageRequest.of(pageNo, PageConst.ARTICLE_PAGE_SIZE);
        given(articleRepository.findAllByGalleryId(any(Long.class), any(Pageable.class))).willReturn(new PageImpl<>(new ArrayList<>(), pageable, 10));


        //when
        Page<Article> articlePage = articleService.findAllByGalleryId(galleryId, pageNo);

        //then
        assertThat(articlePage.getPageable().getPageNumber()).isEqualTo(pageNo);
        verify(articleRepository).findAllByGalleryId(galleryId, pageable);
    }

    @Test
    void findArticleTest(){
        //given
        Long articleId = 1L;
        int i_num = 1;
        Member member = Member.makeSample(i_num);
        Product product = Product.makeSample(1, Company.makeSample(i_num));
        Gallery gallery = Gallery.makeSample(product);
        Article article = Article.makeSample(i_num, gallery, member);
        ReflectionTestUtils.setField(article, "id", articleId);
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        //when
        Article result = articleService.findArticle(articleId);

        //then
        assertThat(result.getId()).isEqualTo(article.getId());
        verify(articleRepository).findById(articleId);
    }

    @Test
    void findAllBySearchWordTest(){
        //given
        ArticleSearch titleArticleSearch = ArticleSearch.of(ArticleSearchTag.TITLE, "title1");
        ArticleSearch contentArticleSearch = ArticleSearch.of(ArticleSearchTag.CONTENT, "content1");
        ArticleSearch nicknameArticleSearch = ArticleSearch.of(ArticleSearchTag.NICKNAME, "nickname1");
        ArticleSearch commentArticleSearch = ArticleSearch.of(ArticleSearchTag.COMMENT, "content1");
        ArticleSearch allArticleSearch = ArticleSearch.of(ArticleSearchTag.ALL, "all1");
        Gallery gallery = Gallery.makeSample(Product.makeSample(1, Company.makeSample(1)));
        Member member = Member.makeSample(1);
        Long galleryId = 1L;
        ReflectionTestUtils.setField(gallery, "id", galleryId);

        List<Article> articles1 = new ArrayList<>();
        articles1.add(Article.of(gallery, member, "titleTest", "titleTest"));
        Page<Article> titleArticlePage = new PageImpl<>(articles1);

        List<Article> articles2 = new ArrayList<>();
        articles2.add(Article.of(gallery, member, "contentTest", "contentTest"));
        Page<Article> contentArticlePage = new PageImpl<>(articles2);

        List<Article> articles3 = new ArrayList<>();
        articles3.add(Article.of(gallery, member, "nicknameTest", "nicknameTest"));
        Page<Article> nicknameArticlePage = new PageImpl<>(articles3);

        List<Article> articles4 = new ArrayList<>();
        articles4.add(Article.of(gallery, member, "commentContentTest", "commentContentTest"));
        Page<Article> commentContentArticlePage = new PageImpl<>(articles4);

        List<Article> articles5 = new ArrayList<>();
        articles5.add(Article.of(gallery, member, "allTest", "allTest"));
        Page<Article> allArticlePage = new PageImpl<>(articles5);

        given(articleRepository.findAllByGalleryIdAndMemberNameOrContentOrTitleContaining(any(Long.class), any(String.class), any(PageRequest.class))).willReturn(allArticlePage);
        given(articleRepository.findAllByGalleryIdAndTitleContaining(any(Long.class), any(String.class), any(PageRequest.class))).willReturn(titleArticlePage);
        given(articleRepository.findAllByGalleryIdAndContentContaining(any(Long.class), any(String.class), any(PageRequest.class))).willReturn(contentArticlePage);
        given(articleRepository.findAllByGalleryIdAndMemberNicknameContaining(any(Long.class), any(String.class), any(PageRequest.class))).willReturn(nicknameArticlePage);
        given(articleRepository.findAllByGalleryIdAndCommentsContentContaining(any(Long.class), any(String.class), any(PageRequest.class))).willReturn(commentContentArticlePage);

        //when
        Page<Article> titleArticlePageResult = articleService.findAllBySearchWord(gallery, titleArticleSearch);
        Page<Article> contentArticlePageResult = articleService.findAllBySearchWord(gallery, contentArticleSearch);
        Page<Article> nicknameArticlePageResult = articleService.findAllBySearchWord(gallery, nicknameArticleSearch);
        Page<Article> commentContentArticlePageResult = articleService.findAllBySearchWord(gallery, commentArticleSearch);
        Page<Article> allArticlePageResult = articleService.findAllBySearchWord(gallery, allArticleSearch);

        //then
        assertThat(titleArticlePageResult.toList().get(0).getTitle()).isEqualTo(articles1.get(0).getTitle());
        assertThat(contentArticlePageResult.toList().get(0).getTitle()).isEqualTo(articles2.get(0).getTitle());
        assertThat(nicknameArticlePageResult.toList().get(0).getTitle()).isEqualTo(articles3.get(0).getTitle());
        assertThat(commentContentArticlePageResult.toList().get(0).getTitle()).isEqualTo(articles4.get(0).getTitle());
        assertThat(allArticlePageResult.toList().get(0).getTitle()).isEqualTo(articles5.get(0).getTitle());
    }

    @Test
    void saveArticleTest(){
        //given
        int sampleNum = 1;
        Long galleryId = 1L;
        String title = "title";
        String content = "content";
        Product product = Product.makeSample(sampleNum, Company.makeSample(sampleNum));
        Gallery gallery = Gallery.makeSample(product);
        ArticleWriteForm articleWriteForm = ArticleWriteForm.of(gallery.getProduct().getName(), title, content);
        Member member = Member.makeSample(sampleNum);
        ReflectionTestUtils.setField(member, "id", 1L);
        Article article = Article.of(gallery, member, title, content);
        given(galleryRepository.findByProduct_Name(gallery.getProduct().getName())).willReturn(Optional.of(gallery));
        given(articleRepository.save(any(Article.class))).willReturn(article);
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        //when
        Article articleResult = articleService.saveArticle(articleWriteForm, member.getId());

        //then
        assertThat(articleResult.getContent()).isEqualTo(article.getContent());
        assertThat(articleResult.getTitle()).isEqualTo(article.getTitle());

        verify(galleryRepository).findByProduct_Name(gallery.getProduct().getName());
        verify(articleRepository).save(any(Article.class));
    }
}