package com.example.steam.module.comment.repository;

import com.example.steam.module.comment.domain.ProfileComment;
import com.example.steam.module.comment.dto.ProfileCommentDto;
import com.example.steam.module.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfileCommentRepository extends JpaRepository<ProfileComment, Long> {
    Page<ProfileComment> findAllByProfileMemberId(Long profileMemberId, PageRequest pageRequest);

    @Query("""
        SELECT new com.example.steam.module.comment.dto.ProfileCommentDto(
        c.id, m.id, m.nickname, m.avatarUrl, c.content, c.createdTime
        )
        FROM ProfileComment c
        JOIN c.member m
        WHERE c.profileMember =:profileMember
        ORDER BY c.createdTime DESC
""")
    Page<ProfileCommentDto> findDtoByProfileMember(@Param("profileMember")Member profileMember, Pageable pageable);
}
