package com.example.steam.core.config.redis;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.resource.DnsResolvers;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisTemplateConfig {

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration
                = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        if(redisProperties.getPassword() != null) {
            redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
        }
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());

        ClientResources resources = DefaultClientResources.builder()
                .dnsResolver(DnsResolvers.JVM_DEFAULT)
                .build();

        ClientOptions options = ClientOptions.builder()
                .timeoutOptions(TimeoutOptions.enabled(Duration.ofSeconds(10)))
                .build();

        LettuceClientConfiguration.LettuceClientConfigurationBuilder cd
                = LettuceClientConfiguration.builder()
                .clientResources(resources)
                .clientOptions(options)
                .commandTimeout(Duration.ofSeconds(2));

        LettuceClientConfiguration lc = cd.build();
        return new LettuceConnectionFactory(redisStandaloneConfiguration, lc);
    }

    @Bean("jsonRedisTemplate")
    public RedisTemplate<String, Object> jsonRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean("chatRedisTemplate")
    public RedisTemplate<String, String> chatRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
