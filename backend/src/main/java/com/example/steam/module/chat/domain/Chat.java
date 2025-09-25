package com.example.steam.module.chat.domain;

import com.example.steam.module.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@SQLDelete(sql="UPDATE chat SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime created;

    @Builder.Default
    private boolean deleted = false;

    public static Chat of(Member member, ChatRoom chatRoom, String content){
        return Chat.builder()
                .member(member)
                .chatRoom(chatRoom)
                .content(content)
                .created(LocalDateTime.now())
                .build();
    }
}
