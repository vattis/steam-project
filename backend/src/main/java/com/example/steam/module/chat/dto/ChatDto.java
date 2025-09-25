package com.example.steam.module.chat.dto;

import com.example.steam.module.chat.domain.Chat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ChatDto {
    private Long id;
    private Long memberId;
    private Long chatRoomId;
    private String content;
    private LocalDateTime created;

    public static ChatDto of(Long id, Long memberId, Long chatRoomId, String content, LocalDateTime created){
        return ChatDto.builder()
                .id(id)
                .memberId(memberId)
                .chatRoomId(chatRoomId)
                .content(content)
                .created(created)
                .build();
    }

    public static ChatDto from(Chat chat){
        return ChatDto.of(chat.getId(),
                chat.getMember().getId(),
                chat.getChatRoom().getId(),
                chat.getContent(),
                chat.getCreated());
    }
}
