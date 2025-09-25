package com.example.steam.module.member.domain;

import com.example.steam.module.product.domain.Product;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql="UPDATE member_game SET deleted = true WHERE id=?") //delete를 사용시, delete=true 업데이트 쿼리를 대신 날린다
@SQLRestriction("deleted = false") //search 사용시 where 절에 delete=false 조건을 추가한다
public class MemberGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int playMinutes;

    @ManyToOne
    private Product product;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member member;

    @Column(nullable = true)
    private LocalDateTime lastPlayedTime;

    @ColumnDefault("false")
    @Builder.Default
    private boolean playing = false;

    @Column(name="deleted", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean deleted = false; //soft delete를 위한 field

    public static MemberGame of(Product product, Member member){
        MemberGame memberGame = MemberGame.builder()
                .product(product)
                .member(member)
                .playMinutes(0)
                .lastPlayedTime(null)
                .build();
        member.getMemberGames().add(memberGame);
        return memberGame;
    }

    private void play(){ //플레이 시간을 재는건 로컬 컴퓨터에서 하도록 하는 것이 맞을듯
        playing = true;
        lastPlayedTime = LocalDateTime.now();
    }
    private void exitGame(){
        playing = false;
    }

}
