package com.example.steam.core.config.redis;

import com.example.steam.module.chat.adapter.RedisChatSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class RedisChatConfig {
    private final RedisConnectionFactory redisConnectionFactory;
    private final RedisChatSubscriber redisChatSubscriber;

    @Bean
    RedisMessageListenerContainer container() {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        redisMessageListenerContainer.addMessageListener(redisChatSubscriber, new PatternTopic("chat:chatRoomId:*"));
        return redisMessageListenerContainer;
    }

}
