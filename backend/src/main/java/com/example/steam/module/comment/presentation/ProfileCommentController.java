package com.example.steam.module.comment.presentation;

import com.example.steam.module.comment.application.ProfileCommentService;
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
public class ProfileCommentController {
    private final ProfileCommentService profileCommentService;
    private final MemberService memberService;

    @PostMapping("/profileComment")
    public String makeProfileComment(
            @RequestParam("memberId") Long memberId,
            @RequestParam("profileId") Long profileId,
            @RequestParam("content") String content) {
        Member member = memberService.findMember(memberId);
        Member profileMember = memberService.findMember(profileId);
        profileCommentService.makeProfileComment(member, profileMember, content);
        return "redirect:/profile/" + profileId;
    }

    @DeleteMapping("/profileComment/{profileComment}")
    public String deleteProfileComment(@PathVariable("profileComment") Long profileCommentId, Principal principal){
        SimpleMemberDto member = memberService.findMemberDtoByEmail(principal.getName());
        profileCommentService.deleteProfileComment(profileCommentId, member.getId());
        return "redirect:/profile/" + member.getId();
    }
}
