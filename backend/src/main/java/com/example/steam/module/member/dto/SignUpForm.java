package com.example.steam.module.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignUpForm {
    private String email;
    private String password;
    private String passwordConfirm;
    private String nickname;
    public boolean isValid(){
        return email != null && password != null && nickname != null && password.equals(passwordConfirm);
    }
    public static SignUpForm of(String email, String password, String passwordConfirm, String nickname){
        return SignUpForm.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .passwordConfirm(passwordConfirm)
                .build();
    }
}
