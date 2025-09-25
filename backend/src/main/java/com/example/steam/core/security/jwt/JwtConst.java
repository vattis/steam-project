package com.example.steam.core.security.jwt;

public class JwtConst {
    public static long JWT_ACCESS_TOKEN_EXPIRES_IN = 10 * 30 * 1000; //30분
    public static long JWT_REFRESH_TOKEN_EXPIRES_IN = 60 * 60 * 7 * 1000; //7시간
}
