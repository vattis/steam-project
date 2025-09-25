package com.example.steam.module.comment.application;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.article.domain.Article;
import com.example.steam.module.article.repository.ArticleRepository;
import com.example.steam.module.comment.domain.ArticleComment;
import com.example.steam.module.comment.repository.ArticleCommentRepository;
import com.example.steam.module.company.domain.Company;
import com.example.steam.module.gallery.domain.Gallery;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {
    @InjectMocks ArticleCommentService articleCommentService;
    @Mock MemberRepository memberRepository;
    @Mock ArticleRepository articleRepository;
    @Mock ArticleCommentRepository articleCommentRepository;

    private Member member;
    private Product product;
    private Company company;
    private Gallery gallery;
    private Article article;
    private String commentContent = "commentContent";
    private ArticleComment articleComment;

    @BeforeEach
    void setup() {
        int sampleNum = 1;
        company = Company.makeSample(sampleNum);
        product = Product.makeSample(sampleNum, company);
        gallery = Gallery.makeSample(product);
        member = Member.makeSample(sampleNum);
        article = Article.makeSample(sampleNum, gallery, member);
        ReflectionTestUtils.setField(article, "id", 1L); // ID 고정
        articleComment = ArticleComment.of(member, article, commentContent);
        ReflectionTestUtils.setField(articleComment, "id", 1L);
        ReflectionTestUtils.setField(member, "id", 1L);
    }

    @Test
    void makeArticleCommentTest() {
        //given
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(articleComment);
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        //when
        ArticleComment articleCommentResult = articleCommentService.makeArticleComment(member.getId(), article, commentContent);

        //then
        assertThat(articleCommentResult.getContent()).isEqualTo(commentContent);
        assertThat(articleCommentResult.getArticle()).isEqualTo(articleComment.getArticle());
        verify(articleCommentRepository).save(any(ArticleComment.class));
    }

    @Test
    void findArticleCommentByArticleIdTest() {
        //given
        List<ArticleComment> articleCommentList = new ArrayList<>();
        int sampleNum2 = 2;
        int sampleNum3 = 3;
        int pageNo = 0;
        int total = 10;
        Pageable pageable = PageRequest.of(pageNo, PageConst.ARTICLE_COMMENT_PAGE_SIZE);
        articleCommentList.add(ArticleComment.makeSample(member, article, sampleNum2));
        articleCommentList.add(ArticleComment.makeSample(member, article, sampleNum3));
        Page<ArticleComment> articleComments = new PageImpl<>(articleCommentList, pageable, total);
        given(articleCommentRepository.findAllByArticleId(any(Long.class), any(PageRequest.class))).willReturn(articleComments);

        //when
        Page<ArticleComment> result = articleCommentService.findArticleCommentByArticleId(article.getId(), pageNo);

        //then
        assertThat(result.stream().allMatch(ac -> ac.getArticle().getId() == article.getId()));
        verify(articleCommentRepository).findAllByArticleId(any(Long.class), any(PageRequest.class));
    }

    @Test
    @DisplayName("댓글 삭제가 성공적으로 이루어지는 경우")
    void deleteArticleCommentTest1() {
        //given

        //when
        boolean result = articleCommentService.deleteArticleComment(articleComment, member.getId());


        //then
        assertThat(result).isTrue();
        assertThat(article.getComments()).doesNotContain(articleComment);
        verify(articleCommentRepository).delete(articleComment);
    }

    @Test
    @DisplayName("댓글 삭제 요청자와 댓글 소유자가 다른 경우 실패")
    void deleteArticleCommentTest2(){
        //given
        int otherSampleNum = 5;
        Member requestMember = Member.makeSample(otherSampleNum);
        ReflectionTestUtils.setField(requestMember, "id", 5L);

        //when
        Boolean result = articleCommentService.deleteArticleComment(articleComment, requestMember.getId());

        //then
        assertThat(result).isFalse();
        verify(articleCommentRepository, never()).delete(any(ArticleComment.class));
    }
}