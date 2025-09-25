package com.example.steam.module.chat.representation;

import com.example.steam.module.chat.application.ChatService;
import com.example.steam.module.chat.dto.ChatDtoScroll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatApiController {
    private final ChatService chatService;

    //채팅방의 이전 채팅 로드
    @GetMapping("/api/chatRoom/{chatRoomId}")
    public ChatDtoScroll chatRoom(@PathVariable Long chatRoomId,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime prevTs,
                                  @RequestParam(required = false) Long prevId,
                                  @RequestParam(required = false, defaultValue = "15") int limit) {
        return chatService.loadChats(chatRoomId, limit, prevTs, prevId);
    }
}
