package com.example.steam.module.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ChatDtoScroll {
    private List<ChatDto> chats;
    private ChatPaging chatPaging;

    public static ChatDtoScroll of(List<ChatDto> chats, ChatPaging chatPaging) {
        return ChatDtoScroll.builder()
                .chats(chats)
                .chatPaging(chatPaging)
                .build();
    }
}
