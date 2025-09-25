package com.example.steam.module.comment.dto;

import com.example.steam.module.comment.domain.ProfileComment;
import com.example.steam.module.member.dto.SimpleMemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class ProfileCommentDto {
    private Long id;
    private Long memberId;
    private String nickname;
    private String avatarUrl;
    private String content;
    private LocalDateTime createdTime;

    public static ProfileCommentDto of(Long id, Long memberId, String nickname, String avatarUrl, String content, LocalDateTime createdAt){
        return ProfileCommentDto
                .builder()
                .id(id)
                .memberId(memberId)
                .nickname(nickname)
                .avatarUrl(avatarUrl)
                .content(content)
                .createdTime(createdAt)
                .build();
    }
    public static ProfileCommentDto from(ProfileComment profileComment) {
        return ProfileCommentDto.of(
                profileComment.getId(),
                profileComment.getMember().getId(),
                profileComment.getMember().getNickname(),
                profileComment.getMember().getAvatarUrl(),
                profileComment.getContent(),
                profileComment.getCreatedTime());
    }
}
