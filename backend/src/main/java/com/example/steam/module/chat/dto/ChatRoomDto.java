package com.example.steam.module.chat.dto;

import com.example.steam.module.chat.domain.ChatRoom;
import com.example.steam.module.member.dto.SimpleMemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomDto {
    private Long id;
    private SimpleMemberDto loginMember;
    private SimpleMemberDto partnerMember;

    public static ChatRoomDto of(Long id, SimpleMemberDto loginMember, SimpleMemberDto partnerMember) {
        return ChatRoomDto.builder()
                .id(id)
                .loginMember(loginMember)
                .partnerMember(partnerMember)
                .build();
    }

    public static ChatRoomDto from(ChatRoom chatRoom, Long loginMemberId) {
        boolean isFirst = chatRoom.getMember1().getId().equals(loginMemberId);

        SimpleMemberDto loginMemberDto
                = SimpleMemberDto.from(isFirst ? chatRoom.getMember1() : chatRoom.getMember2());

        SimpleMemberDto partnerMemberDto
                = SimpleMemberDto.from(isFirst ? chatRoom.getMember2() : chatRoom.getMember1());

        return ChatRoomDto.of(chatRoom.getId(), loginMemberDto, partnerMemberDto);
    }
}
