package com.example.steam.module.friendship.dto;

import com.example.steam.module.friendship.domain.Friendship;
import com.example.steam.module.member.dto.SimpleMemberDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SimpleFriendshipDto {
    private Long id;
    private SimpleMemberDto fromMember;
    private SimpleMemberDto toMember;

    public static SimpleFriendshipDto of(Long id, SimpleMemberDto fromMember, SimpleMemberDto toMember){
        return SimpleFriendshipDto.builder()
                .id(id)
                .fromMember(fromMember)
                .toMember(toMember)
                .build();
    }

    public static SimpleFriendshipDto from(Friendship friendship){
        return SimpleFriendshipDto.of(
                friendship.getId(),
                SimpleMemberDto.from(friendship.getFromMember()),
                SimpleMemberDto.from(friendship.getToMember()));
    }
}
