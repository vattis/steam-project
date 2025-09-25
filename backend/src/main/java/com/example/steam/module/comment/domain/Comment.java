package com.example.steam.module.comment.domain;

import com.example.steam.module.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name="comment")
@DiscriminatorColumn
@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql="UPDATE comment SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
@Inheritance(strategy = InheritanceType.JOINED) ///상속 관계 매핑 사용
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @Lob
    @Column(nullable = false, columnDefinition="TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(name="deleted", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean deleted = false;

}
