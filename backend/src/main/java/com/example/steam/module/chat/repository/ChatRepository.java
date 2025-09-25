package com.example.steam.module.chat.repository;

import com.example.steam.module.chat.domain.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    //채팅방의 최신 N개 메세지 찾기
    List<Chat> findByChatRoom_idOrderByCreatedDescIdDesc(Long chatRoomId, Pageable pageable);

    //커서 이전의 과거 N개의 메세지 찾기
    @Query("""
            select c from Chat c
            where c.chatRoom.id = :chatRoomId
            and ((c.created < :prevTs) or (c.created = :prevTs and c.id < :prevId))
            order by c.created desc, c.id desc
            """)
    List<Chat> findHistoryChat(@Param("chatRoomId") Long chatRoomId,
                               @Param("prevTs") LocalDateTime prevTs,
                               @Param("prevId") Long prevId,
                               Pageable pageable);

    @Query(value = """
           select (case when count(c) > 0 then true else false end)
           from Chat c
           where c.chatRoom.id = :chatRoomId
           and ((c.created < :prevTs) or (c.created = :prevTs and c.id < :prevId))
           """)
    boolean existsHistoryChat(@Param("chatRoomId") Long chatRoomId,
                              @Param("prevTs") LocalDateTime prevTs,
                              @Param("prevId") Long prevId);
}
