package com.example.steam.module.comment.presentation;

import com.example.steam.module.comment.application.ProductCommentService;
import com.example.steam.module.comment.domain.ProductComment;
import com.example.steam.module.member.application.MemberService;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.dto.SimpleMemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ProductCommentController {
    private final ProductCommentService productCommentService;
    private final MemberService memberService;


    @PostMapping("/productComment/{productId}")
    String postProductComment(@PathVariable("productId") Long productId,
                              @RequestParam("content") String content,
                              @RequestParam("rating") Float rate,
                              Principal principal){
        SimpleMemberDto member = memberService.findMemberDtoByEmail(principal.getName());
        productCommentService.makeProductComment(member.getId(), productId, content, rate);
        return "redirect:/product/"+productId;
    }
    @DeleteMapping("/productComment/{productCommentId}")
    String deleteProductComment(@PathVariable("productCommentId") Long productCommentId, Principal principal){
        SimpleMemberDto member = memberService.findMemberDtoByEmail(principal.getName());
        ProductComment productComment = productCommentService.findById(productCommentId);
        productCommentService.deleteProductComment(productComment, member.getId());
        return "redirect:/product/" + productComment.getProduct().getId();
    }
}
