package com.example.steam.module.product.domain;

import lombok.Getter;

@Getter
public enum ProductSearchTag {
    ALL("ALL"), NAME("NAME"), COMPANY("COMPANY");

    String label;

    ProductSearchTag(String tag) {

    }
    public static ProductSearchTag makeTag(String tag) {
        if(tag.equals("ALL") || tag.equals("all")){
            return ProductSearchTag.ALL;
        }else if(tag.equals("NAME") || tag.equals("name")){
            return ProductSearchTag.NAME;
        }else if(tag.equals("COMPANY") || tag.equals("company")){
            return ProductSearchTag.COMPANY;
        }
        return null;
    }
}
