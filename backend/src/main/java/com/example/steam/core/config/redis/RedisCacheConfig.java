package com.example.steam.core.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfig {
    @Bean
    RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        ObjectMapper om = objectMapper.copy();
        om.findAndRegisterModules();


        RedisCacheConfiguration redisCacheConfiguration
                = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .prefixCacheNameWith("steam-cache::");

        Map<String, RedisCacheConfiguration> cacheConfig = new HashMap<>();
        cacheConfig.put("login-member::email:memberDto", redisCacheConfiguration.entryTtl(Duration.ofMinutes(30)));
        cacheConfig.put("login-member::jwt:authDto", redisCacheConfiguration.entryTtl(Duration.ofMinutes(10)));
        cacheConfig.put("blacklist::jwt:time", redisCacheConfiguration.entryTtl(Duration.ofMinutes(60*7)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfig)
                .transactionAware()
                .build();
    }
}
