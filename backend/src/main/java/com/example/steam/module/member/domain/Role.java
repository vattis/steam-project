package com.example.steam.module.member.domain;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

    private final String label;

    Role(String role) {
        this.label = role;
    }
}
