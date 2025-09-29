package com.example.steam.core.config.redis;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.resource.DnsResolvers;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.time.Duration;

@Profile("embedded_redis")
@Configuration
public class EmbeddedRedisConfig {

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


    private RedisServer redisServer;
    @Value("${spring.data.redis.port}") private int redisPort;

    @PostConstruct
    void start() throws IOException {
        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    @PreDestroy
    private void stop(){
        redisServer.stop();
    }
}
