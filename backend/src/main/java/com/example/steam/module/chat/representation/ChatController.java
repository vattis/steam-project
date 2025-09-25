package com.example.steam.module.chat.representation;

import com.example.steam.module.chat.adapter.RedisChatPublisher;
import com.example.steam.module.chat.application.ChatRoomService;
import com.example.steam.module.chat.application.ChatService;
import com.example.steam.module.member.application.MemberService;
import com.example.steam.module.member.dto.SimpleMemberDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Profile("!load")
@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final MemberService memberService;
    private final ChatRoomService chatRoomService;
    private final RedisChatPublisher redisChatPublisher;


    //채팅 보내기
    @MessageMapping("/chat/{chatRoomId}/send")
    public void sendChat(@DestinationVariable Long chatRoomId,
                           @Payload String content,
                         Principal principal) throws JsonProcessingException {
        log.info("[STOMP] SEND hit: room={}, payload='{}'", chatRoomId, content);
        SimpleMemberDto member = memberService.findMemberDtoByEmail(principal.getName());
        redisChatPublisher.publishMessage(chatRoomId, member.getId(), content);
    }
}
