package com.example.steam.module.chat.adapter;

import com.example.steam.module.chat.dto.ChatDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisChatSubscriber implements MessageListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper mapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        String chatRoomId = channel.substring("chat:chatRoomId:".length());

        ChatDto chatDto = null;
        try {
            chatDto = mapper.readValue(payload, ChatDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("[REDIS] recv chan={}, body='{}'", channel, payload);
        messagingTemplate.convertAndSend("/sub/chat/" + chatRoomId, chatDto);
        log.info("[STOMP] broadcast to /sub/chat/{}", chatRoomId);
        log.debug("[WS BROADCAST] {} <- {}", chatRoomId, chatDto.getId());
    }
}
