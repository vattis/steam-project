package com.example.steam.module.chat.representation;

import com.example.steam.module.chat.application.ChatRoomService;
import com.example.steam.module.chat.application.ChatService;
import com.example.steam.module.chat.domain.ChatRoom;
import com.example.steam.module.chat.dto.ChatRoomDto;
import com.example.steam.module.member.application.MemberService;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.dto.SimpleMemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    private final MemberService memberService;

    //채팅방 입장
    @GetMapping("/chatRoom/{partnerId}")
    private String gotoChatRoom(@PathVariable Long partnerId, Principal principal, Model model){
        SimpleMemberDto loginMember = memberService.findMemberDtoByEmail(principal.getName());
        ChatRoom chatRoom = chatRoomService.getChatRoomByMemberIds(partnerId, loginMember.getId());
        if(!chatRoom.checkMember(loginMember.getId())){
            log.info("권한이 없는 채팅방 입장 시도: chatRoom = {} , member = {}", chatRoom.getId(), loginMember.getId());
            return "main";
        }
        model.addAttribute("chatRoomDto", ChatRoomDto.from(chatRoom, loginMember.getId()));
        return "chat/chat-room";
    }
}
