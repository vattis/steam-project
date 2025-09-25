package com.example.steam.module.chat.representation;

import com.example.steam.module.chat.adapter.RedisChatPublisher;
import com.example.steam.module.chat.application.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Profile("load")
@Controller
@Slf4j
@RequiredArgsConstructor
public class LoadTestChatController {
    private final RedisChatPublisher redisChatPublisher;
    @MessageMapping("/chat/{chatRoomId}/send")
    public void sendLoadTest(@DestinationVariable("chatRoomId") Long chatRoomId, @Payload String content) throws JsonProcessingException {
        log.info("[STOMP] SEND hit: room={}, payload='{}'", chatRoomId, content);
        redisChatPublisher.publishMessage(chatRoomId, 100001L, content);
    }
}
