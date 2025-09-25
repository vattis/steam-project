package com.example.steam.module.chat.adapter;

import com.example.steam.module.chat.application.ChatService;
import com.example.steam.module.chat.dto.ChatDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisChatPublisher {
    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    public RedisChatPublisher(ChatService chatService, ObjectMapper objectMapper, @Qualifier("chatRedisTemplate") RedisTemplate<String, String> redisTemplate){
        this.chatService = chatService;
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }


    public void publishMessage(Long chatRoomId, Long memberId, String message) throws JsonProcessingException {
        ChatDto chatDto = chatService.sendMessage(chatRoomId, memberId, message);
        try{
            log.info("[WS OUT] /sub/chat/{} -> {}", chatRoomId, chatDto);
            log.info("[REDIS] publish room={}, body='{}'", chatRoomId, message);
            redisTemplate.convertAndSend("chat:chatRoomId:" + chatRoomId, objectMapper.writeValueAsString(chatDto));
        }catch(JsonProcessingException e){
            log.error("메세지 직렬화 실패: {}", chatRoomId, e);
            throw new IllegalStateException("메세지 직렬화 오류", e);
        }
    }
}
