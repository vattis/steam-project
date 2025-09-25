package com.example.steam.module.friendship.presentation;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.friendship.application.FriendshipService;
import com.example.steam.module.friendship.domain.FriendshipState;
import com.example.steam.module.friendship.dto.SimpleFriendshipDto;
import com.example.steam.module.member.application.MemberService;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.dto.SimpleMemberDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FriendshipController {

    private final MemberService memberService;
    private final FriendshipService friendshipService;
    //친구 목록 조회
    @GetMapping("/friendships/{loginMemberId}")
    public String showFriendships(@PathVariable("loginMemberId") Long loginMemberId,
                                  @RequestParam(name = "pageNo", required = false, defaultValue = "0") int pageNo,
                                  Principal principal, Model model){
        SimpleMemberDto member = memberService.findMemberDtoByEmail(principal.getName());
        if(!loginMemberId.equals(member.getId())){
            log.info("잘못된 친구 목록 요청: 로그인 사용자 불일치 || loginMemberId ={}  requestMemberId ={}", member.getId(), loginMemberId);
            return "redirect:/";
        }
        Page<SimpleFriendshipDto> friends = friendshipService.getFriends(loginMemberId, PageRequest.of(pageNo, PageConst.FRIENDS_PAGE_SIZE)).map(SimpleFriendshipDto::from);
        model.addAttribute("friends", friends);
        return "friend/friends";
    }

    // 받은 친구 신청 조회 (INVITED로만 조회)
    @GetMapping("/friendships")
    public String showInvitedFriendships(
            @RequestParam(name = "state", required = false, defaultValue = "INVITED")
            FriendshipState state,
            Model model,
            Principal principal) {
        if (state != FriendshipState.INVITED) {
            return "redirect:/friendships?state=INVITED";
        }

        SimpleMemberDto loginMember = memberService.findMemberDtoByEmail(principal.getName());
        List<SimpleFriendshipDto> result = friendshipService.getFriendRequest(loginMember.getId())
                .stream().map(SimpleFriendshipDto::from).toList();

        model.addAttribute("result", result);
        return "friend/invited-friendship"; // templates/friends-invitations.html
    }

    // 친구 신청 (POST)
    @PostMapping("/friendships")
    public String addFriendship(@RequestParam("toMemberId") Long toMemberId,
                                Principal principal,
                                @RequestParam(value = "redirect", required = false) String redirect,
                                HttpServletRequest req,
                                RedirectAttributes ra) {
        SimpleMemberDto loginMember = memberService.findMemberDtoByEmail(principal.getName());
        friendshipService.inviteFriend(loginMember.getId(), toMemberId);
        ra.addFlashAttribute("msg", "친구 초대를 보냈습니다.");

        // 안전한 내부 경로로만 리다이렉트
        return "redirect:" + resolveRedirect(redirect, req, "/members");
    }

    //친구 수락
    @PatchMapping("/friendships/{toMemberId}")
    public String acceptFriendship(@PathVariable Long toMemberId, Principal principal,
                                   @RequestParam(value = "redirect", required = false) String redirect,
                                   HttpServletRequest req){
        SimpleMemberDto loginMember = memberService.findMemberDtoByEmail(principal.getName());
        friendshipService.acceptFriend(loginMember.getId(), toMemberId);
        return "redirect:" + resolveRedirect(redirect, req, "/members");
    }

    //친구 신청 거절
    @DeleteMapping("/friendships/{toMemberId}")
    public String declineFriendship(@PathVariable Long toMemberId, Principal principal,
                                    @RequestParam(value = "redirect", required = false) String redirect,
                                    HttpServletRequest req){
        SimpleMemberDto loginMember = memberService.findMemberDtoByEmail(principal.getName());
        friendshipService.removeFriendship(loginMember.getId(), toMemberId);
        return "redirect:" + resolveRedirect(redirect, req, "/members");
    }




    /** 외부로 튀는 오픈 리다이렉트 방지 + Referer fallback */
    private String resolveRedirect(String redirect, HttpServletRequest req, String fallback) {
        if (redirect != null && redirect.startsWith("/") && !redirect.startsWith("//") && !redirect.contains("://")) {
            return redirect; // 내부 상대경로만 허용
        }
        String ref = req.getHeader("Referer");
        if (ref != null) {
            try {
                URI uri = URI.create(ref);
                String q = (uri.getQuery() == null) ? "" : "?" + uri.getQuery();
                return uri.getPath() + q; // 같은 앱 내 경로만 사용
            } catch (IllegalArgumentException ignore) {}
        }
        return fallback;
    }
}