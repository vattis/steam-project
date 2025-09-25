package com.example.steam.module.chat.domain;

import com.example.steam.module.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
@Check(constraints = "member1_id <> member2_id") //자신과의 채팅 금지
@SQLDelete(sql="UPDATE chat_room SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member1_id", nullable = false)
    private Member member1;

    @ManyToOne
    @JoinColumn(name = "member2_id", nullable = false)
    private Member member2;

    @Column
    private LocalDateTime created;

    @Builder.Default
    private boolean deleted = false;

    @PrePersist
    @PreUpdate
    void normalizeChatMember(){ //항상 member1 쪽의 memberId가 작도록 유지(채팅창 중복 방지)
        if(member1 == null || member2 == null){
            throw new IllegalStateException("member1 or member2 are null");
        }
        Long a = member1.getId(), b = member2.getId();
        if(a.equals(b)){
            throw new IllegalStateException("self chat is not allowed");
        }
        if(a > b){
            Member temp = member1;
            member1 = member2;
            member2 = temp;
        }
    }

    public static ChatRoom of(Member member1, Member member2){
        if(member1.getId().equals(member2.getId())){
            log.info("잘못된 채팅방 생성 요청: 두 Member 가 일치함");
            throw new IllegalArgumentException();
        }
        return ChatRoom.builder()
                .member1(member1)
                .member2(member2)
                .created(LocalDateTime.now())
                .build();
    }

    public boolean checkMember(Long memberId){
        return member1.getId().equals(memberId) || member2.getId().equals(memberId);
    }
}
