package com.example.steam.module.product.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductSearch {
    ProductSearchTag searchTag;
    String searchWord;

    public static ProductSearch of(ProductSearchTag searchTag, String searchWord) {
        return ProductSearch.builder()
                .searchWord(searchWord)
                .searchTag(searchTag)
                .build();
    }
}
