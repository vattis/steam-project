package com.example.steam.module.comment.application;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.comment.domain.ProductComment;
import com.example.steam.module.comment.repository.ProductCommentRepository;
import com.example.steam.module.company.domain.Company;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.domain.MemberGame;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.repository.ProductRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductCommentServiceTest {
    @InjectMocks ProductCommentService productCommentService;
    @Mock ProductRepository productRepository;
    @Mock ProductCommentRepository productCommentRepository;
    @Mock MemberRepository memberRepository;

    private Member member;
    private Company company;
    private Product product;
    private ProductComment productComment;

    @BeforeEach
    void setUp(){
        int sampleNum = 1;
        String commentContent = "productCommentContent";
        Float rate = 4.5f;
        member = Member.makeSample(sampleNum);
        company = Company.makeSample(sampleNum);
        product = Product.makeSample(sampleNum, company);
        productComment = ProductComment.of(product, member, commentContent, rate);
        ReflectionTestUtils.setField(product, "id", 1L);
        ReflectionTestUtils.setField(member, "id", 1L);
    }

    @Test
    void makeProductCommentTest() {
        //given
        MemberGame.of(product, member);
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(productCommentRepository.save(any(ProductComment.class))).willReturn(productComment);
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        //when
        productCommentService.makeProductComment(member.getId(), product.getId(), productComment.getContent(), productComment.getRate());

        //then
        verify(productRepository).findById(product.getId());
        verify(productCommentRepository).save(any(ProductComment.class));
    }

    @Test
    void findProductCommentByProductIdTest() {
        //given
        int sampleNum3 = 3;
        int sampleNum4 = 4;
        int pageNo = 0;
        List<ProductComment> productCommentList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(pageNo, PageConst.PRODUCT_COMMENT_PAGE_SIZE);
        productCommentList.add(ProductComment.makeSample(product, member, sampleNum3));
        productCommentList.add(ProductComment.makeSample(product, member, sampleNum4));
        Page<ProductComment> productCommentPage = new PageImpl<>(productCommentList, pageRequest, 10);
        given(productCommentRepository.findAllByProductId(product.getId(), pageRequest)).willReturn(productCommentPage);

        //when
        Page<ProductComment> result = productCommentService.findProductCommentByProductId(product.getId(), pageNo);

        //then
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.stream().allMatch(pc -> Objects.equals(pc.getProduct().getId(), product.getId()))).isTrue();
        verify(productCommentRepository).findAllByProductId(product.getId(), pageRequest);
    }

    @Test
    @DisplayName("지우려는 사람과 댓글 소유자가 일치하는 경우 성공")
    void deleteProductCommentTest1() {
        //given

        //when
        boolean result = productCommentService.deleteProductComment(productComment, member.getId());


        //then
        assertThat(result).isTrue();
        assertThat(productComment.getProduct().getProductComments().isEmpty()).isTrue();
        verify(productCommentRepository).delete(productComment);
    }

    @Test
    @DisplayName("지우려는 사람과 댓글 소유자가 일치하지 않는 경우 실패")
    void deleteProductCommentTest2() {
        //given
        int otherSample = 5;
        Member otherMember = Member.makeSample(otherSample);
        ReflectionTestUtils.setField(member, "id", 5L);

        //when
        boolean result = productCommentService.deleteProductComment(productComment, otherMember.getId());

        //then
        assertThat(result).isFalse();
        verify(productCommentRepository, never()).delete(productComment);
    }
}