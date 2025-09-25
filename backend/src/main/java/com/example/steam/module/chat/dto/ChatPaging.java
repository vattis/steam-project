package com.example.steam.module.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ChatPaging {
    boolean hasPrev;
    LocalDateTime prevCursorTime;
    Long prevCursorId;

    public static ChatPaging of(boolean hasPrev, LocalDateTime prevCursorTime, Long prevCursorId) {
        return ChatPaging.builder()
                .hasPrev(hasPrev)
                .prevCursorTime(prevCursorTime)
                .prevCursorId(prevCursorId)
                .build();
    }
}
