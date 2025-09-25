package com.example.steam.module.member.presentation;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.email.application.EmailService;
import com.example.steam.module.friendship.application.FriendshipService;
import com.example.steam.module.member.application.MemberGameService;
import com.example.steam.module.member.application.MemberService;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.dto.MemberGameDto;
import com.example.steam.module.member.dto.MemberSearch;
import com.example.steam.module.member.dto.ProfileDto;
import com.example.steam.module.member.dto.SimpleMemberDto;
import com.example.steam.module.member.repository.MemberGameRepository;
import com.example.steam.module.member.repository.MemberRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.NoSuchElementException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final EmailService emailService;
    private final MemberRepository memberRepository;
    private final MemberGameService memberGameService;
    private final MemberGameRepository memberGameRepository;
    private final FriendshipService friendshipService;
    private int tempAuthNum; //임시 인증 번호 저장소 이후에 redis에 저장 예정

    @ResponseBody
    @PostMapping("/auth/sendEmail/{email}") //사용자 이메일 인증 이메일 전송
    public int sendAuthEmail(@PathVariable("email") String email) {
        int authNum = memberService.createAuthCode();
        String body = "";
        body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
        body += "<h1>" + authNum + "</h1>";
        body += "<h3>" + "감사합니다." + "</h3>";
        try {
            emailService.sendEmail(email, "steam 인증입니다.", body);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        tempAuthNum = authNum;
        return authNum;
    }

    @GetMapping("/auth/check/{email}/{authCode}") //사용자 이메일 인증 숫자 확인
    @ResponseBody
    public ResponseEntity<?> checkAuth(@PathVariable("authCode") String authNum) {
        return ResponseEntity.ok(tempAuthNum == Integer.parseInt(authNum));
    }

    @GetMapping({"/library/{memberId}", "/library/{memberId}/{selectedGameId}"}) //유저 라이브러리
    public String gotoLibrary(@PathVariable("memberId") Long memberId, @PathVariable(value = "selectedGameId", required = false) Long selectedGameId, Model model, Principal principal) {
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        if(principal != null && !member.getEmail().equals(principal.getName())){ //다른 사용자의 접근 차단
            log.info("[MemberController] 일치하지 않은 유저의 라이브러리 접근 확인");
            log.info("member email: " + member.getEmail() + ", profileMember email: " + principal.getName());
            return "redirect:/";
        }
        if(selectedGameId != null){
            if(!memberGameService.isOwned(member, selectedGameId)){
                log.info("[MemberController] 소유하지 않은 MemberGame 접근 시도");
                log.info("member email: " + member.getEmail());
                return "redirect:/";
            }
        }
        model.addAttribute("games", memberGameService.getMemberGameDtosByMember(member));
        if(selectedGameId == null){
            model.addAttribute("selectedGame", null);
        }else{
            MemberGameDto selectedGame = memberGameService.findDtoById(selectedGameId);
            model.addAttribute("selectedGame", selectedGame);
        }
        return "member/library";
    }

    //유저 프로필 이동
    @GetMapping("/profile/{memberId}")
    public String gotoMyPage(@PathVariable("memberId") Long memberId, @RequestParam(name = "commentPageNum", required = false, defaultValue = "0") int commentPageNum, Model model) {
        ProfileDto profileDto = memberService.getProfile(memberId, PageRequest.of(commentPageNum, PageConst.PROFILE_COMMENT_PAGE_SIZE));
        model.addAttribute("profileDto", profileDto);
        return "member/profile";
    }

    @GetMapping("/members/search")
    public String gotoMemberSearchPage(){
        return "member/member-search";
    }

    @GetMapping("/members")
    public String searchMember(@RequestParam(name = "searchTag",  defaultValue = "nickname") String searchTag,
                               @RequestParam(name = "searchWord", defaultValue = "") String searchWord,
                               @RequestParam(name = "pageNo",     defaultValue = "0") int pageNo,
                               Principal principal,
                               Model model){
        Page<SimpleMemberDto> simpleMemberDtos =  memberService.searchMember(MemberSearch.of(searchTag, searchWord), pageNo, principal).map(SimpleMemberDto::from);
        Page<SimpleMemberDto> result = memberService.attachFriendState(simpleMemberDtos, principal);
        model.addAttribute("members", result);
        return "member/member-search";
    }
}
