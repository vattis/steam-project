package com.example.steam.module.member.dto;

import com.example.steam.module.comment.dto.ProfileCommentDto;
import com.example.steam.module.friendship.dto.SimpleFriendshipDto;
import com.example.steam.module.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
    private SimpleMemberDto profileMember;
    private List<MemberGameDto> simpleMemberGames;
    private Page<ProfileCommentDto> profileCommentPage;
    private List<SimpleFriendshipDto> friendships;

    public static ProfileDto of(Member member, List<MemberGameDto> simpleMemberGames, Page<ProfileCommentDto> profileCommentPage, List<SimpleFriendshipDto> friendships) {
        return ProfileDto.builder()
                .profileMember(SimpleMemberDto.from(member))
                .simpleMemberGames(simpleMemberGames)
                .profileCommentPage(profileCommentPage)
                .friendships(friendships)
                .build();
    }
}
