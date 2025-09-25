package com.example.steam.core.security.jwt;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JwtBlackList { //계속 메모리에 토큰이 누적되겠지만, 나중에 redis로 바꿀예정임

    @Cacheable(cacheNames = "blacklist::jwt:time",
            key = "#token", unless = "#result == null")
    public Long isBlacklist(String token, long time) {
        return time;
    }

}
