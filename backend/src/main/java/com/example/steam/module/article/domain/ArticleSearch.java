package com.example.steam.module.article.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ArticleSearch {
    ArticleSearchTag tag;
    String searchWord;

    public static ArticleSearch of(ArticleSearchTag tag, String searchWord){
        return ArticleSearch.builder()
                .tag(tag)
                .searchWord(searchWord)
                .build();
    }
}
