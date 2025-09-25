package com.example.steam.module.login.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LoginForm {
    private String email;
    private String password;

    public static LoginForm of(String email, String password) {
        return LoginForm.builder()
                .email(email)
                .password(password)
                .build();
    }
    public static LoginForm of(){
        return LoginForm.builder()
                .build();
    }
}
