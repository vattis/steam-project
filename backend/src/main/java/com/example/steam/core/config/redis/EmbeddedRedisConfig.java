package com.example.steam.core.config.redis;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import java.io.IOException;

@Profile("embedded_redis")
@Configuration
public class EmbeddedRedisConfig {
    private RedisServer redisServer;
    @Value("${spring.data.redis.port}") private int redisPort;

    @PostConstruct
    void start() throws IOException {
        redisServer = new RedisServer(redisPort);
    }

    @PreDestroy
    private void stop(){
        redisServer.stop();
    }
}
