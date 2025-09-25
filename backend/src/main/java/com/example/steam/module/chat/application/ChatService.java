package com.example.steam.module.chat.application;

import com.example.steam.module.chat.domain.Chat;
import com.example.steam.module.chat.domain.ChatRoom;
import com.example.steam.module.chat.dto.ChatDto;
import com.example.steam.module.chat.dto.ChatDtoScroll;
import com.example.steam.module.chat.dto.ChatPaging;
import com.example.steam.module.chat.repository.ChatRepository;
import com.example.steam.module.chat.repository.ChatRoomRepository;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisTemplate<String, Object> redisTemplate;


    //메세지 보내기
    public ChatDto sendMessage(Long chatRoomId, Long memberId, String message){
        if(message.length() > 400){
            log.info("너무 긴 메세지 전송 시도");
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE);
        }
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(NoSuchElementException::new);
        if(!chatRoom.checkMember(memberId)){
            log.info("권한이 없는 채팅 시도: 채팅 권한이 없는 유저의 채팅 : ChatRoom = {}, memberId = {}, memberId1 = {}, memberId2 = {}",
                    chatRoomId, memberId, chatRoom.getMember1().getId(), chatRoom.getMember2().getId());
            throw new AccessDeniedException("채팅 권한 없음");
        }
        Chat chat = chatRepository.save(Chat.of(member, chatRoom, message));
        return ChatDto.from(chat);
    }

    //채팅방 메세지 불러오기
    public ChatDtoScroll loadChats(Long chatRoomId, int limit, LocalDateTime prevTs, Long prevId){  //limit:한번에 불러올 채팅량, prevTs:직전까지 로드한 메세지의 시간, prevId:직전까지 로드한 메세지의 id
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "created"));

        //이전 메세지를 로드한 적이 없다면 그냥 꺼내오고 로드한 적이 있다면 이전에 로드한 메세지를 기준으로 로드
        List<Chat> chats = (prevTs == null || prevId == null) ?
                chatRepository.findByChatRoom_idOrderByCreatedDescIdDesc(chatRoomId, pageable)
                : chatRepository.findHistoryChat(chatRoomId, prevTs, prevId, pageable);

        List<ChatDto> chatDtos = new ArrayList<>(chats.stream().map(ChatDto::from).toList());
        Collections.reverse(chatDtos);

        //다음 커서
        LocalDateTime prevCursorTime = chats.isEmpty() ? null : chats.get(0).getCreated();
        Long prevCursorId = chats.isEmpty() ? null : chats.get(0).getId();
        boolean hasPrev = chatRepository.existsHistoryChat(chatRoomId, prevCursorTime, prevCursorId);
        ChatPaging chatPaging = ChatPaging.of(hasPrev, prevCursorTime, prevCursorId);
        return ChatDtoScroll.of(chatDtos, chatPaging);
    }
}
