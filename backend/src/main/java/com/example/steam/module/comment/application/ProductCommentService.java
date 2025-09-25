package com.example.steam.module.comment.application;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.comment.domain.ProductComment;
import com.example.steam.module.comment.repository.ProductCommentRepository;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.domain.MemberGame;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProductCommentService {
    private final ProductRepository productRepository;
    private final ProductCommentRepository productCommentRepository;
    private final MemberRepository memberRepository;

    //댓글 id로 찾기
    public ProductComment findById(Long productCommentId){
        return productCommentRepository.findById(productCommentId).orElseThrow(NoSuchElementException::new);
    }

    //댓글 달기
    public ProductComment makeProductComment(Long memberId, Long productId, String content, Float rate){
        Product product = productRepository.findById(productId).orElseThrow(NoSuchElementException::new);
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        for(MemberGame memberGame : member.getMemberGames()){//해당 상품을 갖고 있는 유저인지 확인
            if(memberGame.getProduct().getId().equals(product.getId())){
                ProductComment productComment = ProductComment.of(product, member, content, rate);
                return productCommentRepository.save(productComment);
            }
        }
        log.info("ProductComment 생성 실패::권한이 없는 유저");
        return null;
    }

    //게임id로 게임 후기 찾기
    public Page<ProductComment> findProductCommentByProductId(Long productId, int pageNum){
        PageRequest pageRequest = PageRequest.of(pageNum, PageConst.PRODUCT_COMMENT_PAGE_SIZE);
        return productCommentRepository.findAllByProductId(productId, pageRequest);
    }

    //댓글 삭제
    public boolean deleteProductComment(ProductComment productComment, Long memberId){
        if(!productComment.getMember().getId().equals(memberId)){ //댓글 작성자와 삭제 요청자가 다른 경우
            log.info("잘못된 ProductComment 삭제:: 회원 불일치");
            return false;
        }
        Product product = productComment.getProduct();
        product.getProductComments().remove(productComment);
        productCommentRepository.delete(productComment);
        return true;
    }
}
