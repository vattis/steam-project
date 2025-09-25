package com.example.steam.module.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberSearch {
    String searchTag;
    String searchWord;
    public static MemberSearch of(String searchTag, String searchWord){
        return MemberSearch.builder()
                .searchTag(searchTag)
                .searchWord(searchWord)
                .build();
    }
}
