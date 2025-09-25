package com.example.steam.module.chat.application;

import com.example.steam.module.chat.domain.ChatRoom;
import com.example.steam.module.chat.repository.ChatRepository;
import com.example.steam.module.chat.repository.ChatRoomRepository;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final ChatService chatService;
    private final MemberRepository memberRepository;

    //멤버 2명을 넣어서 해당하는 ChatRoom 이 존재하면 그대로 반환, 존재하지 않는다면 생성하고 반환
    public ChatRoom getChatRoomByMemberIds(Long member1Id, Long member2Id) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findByMembers(member1Id, member2Id);
        Member member1 = memberRepository.findById(member1Id).orElseThrow(NoSuchElementException::new);
        Member member2 = memberRepository.findById(member2Id).orElseThrow(NoSuchElementException::new);
        if(chatRoomOptional.isEmpty()) {
            ChatRoom chatRoom;
            if(member1Id.equals(member2Id)) {
                throw new IllegalStateException("self chat room 생성 불가");
            }
            else if(member1Id > member2Id){
                chatRoom = ChatRoom.of(member2, member1);
            }else{
                chatRoom = ChatRoom.of(member1, member2);
            }
            return chatRoomRepository.save(chatRoom);
        }
        return chatRoomOptional.get();
    }
}
