package com.example.steam.module.comment.presentation;

import com.example.steam.module.article.application.ArticleService;
import com.example.steam.module.article.domain.Article;
import com.example.steam.module.comment.application.ArticleCommentService;
import com.example.steam.module.comment.domain.ArticleComment;
import com.example.steam.module.member.application.MemberService;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.dto.SimpleMemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ArticleCommentController {
    private final MemberService memberService;
    private final ArticleService articleService;
    private final ArticleCommentService articleCommentService;

    @PostMapping("/articleComment")
    public String postArticleComment(@RequestParam("articleId") Long articleId, @RequestParam("commentContent") String articleCommentContent, Principal principal){
        SimpleMemberDto member = memberService.findMemberDtoByEmail(principal.getName());
        Article article = articleService.findArticle(articleId);
        articleCommentService.makeArticleComment(member.getId(), article, articleCommentContent);
        return "redirect:/article/" + articleId;
    }

    @DeleteMapping("/articleComment/{articleCommentId}")
    public String deleteArticleComment(@PathVariable("articleCommentId") Long articleCommentId, Principal principal){
        SimpleMemberDto member = memberService.findMemberDtoByEmail(principal.getName());
        ArticleComment articleComment = articleCommentService.findById(articleCommentId);
        Article article = articleComment.getArticle();
        articleCommentService.deleteArticleComment(articleComment, member.getId());
        return "redirect:/article/" + article.getId();
    }
}
