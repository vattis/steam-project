package com.example.steam.module.friendship.domain;

import com.example.steam.module.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

//친구 관계 엔티티
//서로 친구일 경우 fromMember = member1, toMember = member2
//fromMember = member2, toMember = member1
//이런식으로 양방향으로 엔티티가 생기도록 함
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@SQLDelete(sql="UPDATE friendship SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //자신
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member fromMember;

    //상대
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member toMember;
    
    //친구 수락 여부
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipState state;  // INVITE_SENT, INVITED, FRIENDS, NONE

    @Column(name="deleted", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean deleted = false;

    public static Friendship of(Member fromMember, Member toMember, FriendshipState state){
        return Friendship.builder()
                .fromMember(fromMember)
                .toMember(toMember)
                .state(state)
                .build();
    }
    public static Friendship createReverseFriendship(Friendship friendship, FriendshipState friendshipState){
        return Friendship.of(friendship.toMember, friendship.fromMember, friendshipState);
    }
    public Friendship acceptFriendship(){
        state = FriendshipState.FRIENDS;
        return this;
    }
    public Friendship rejectFriendship(){
        state = FriendshipState.NONE;
        return this;
    }
}
