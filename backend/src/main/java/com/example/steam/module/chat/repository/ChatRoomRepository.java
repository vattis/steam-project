package com.example.steam.module.chat.repository;

import com.example.steam.module.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("""
        select (case when count(cr) > 0 then true else false end)
        from ChatRoom cr
        where (cr.member1 = :member1Id and cr.member2 = :member2Id)
        or (cr.member2 = :member1Id and cr.member1 = :member2Id)
""")
    boolean findIfChatRoomExistsByMembers(Long member1Id, Long member2Id);

    @Query("""
        select cr from ChatRoom cr
        where (cr.member1.id = :member1Id and cr.member2.id = :member2Id)
        or (cr.member2.id = :member1Id and cr.member1.id = :member2Id)
""")
    Optional<ChatRoom> findByMembers(Long member1Id, Long member2Id);
}
