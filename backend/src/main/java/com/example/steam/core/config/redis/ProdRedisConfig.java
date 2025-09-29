package com.example.steam.core.config.redis;


import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.resource.DnsResolvers;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Configuration
@Profile("redis")
public class ProdRedisConfig {
    @Bean
    public LettuceConnectionFactory redisConnectionFactory(RedisProperties p) {
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration(p.getHost(), p.getPort());
        if (p.getPassword() != null && !p.getPassword().isEmpty()) {
            conf.setPassword(RedisPassword.of(p.getPassword()));
        }
        conf.setDatabase(p.getDatabase());

        ClientResources resources = DefaultClientResources.builder()
                .dnsResolver(DnsResolvers.JVM_DEFAULT)
                .build();

        ClientOptions options = ClientOptions.builder()
                .autoReconnect(true)
                .timeoutOptions(TimeoutOptions.enabled(Duration.ofSeconds(10)))
                .socketOptions(SocketOptions.builder().connectTimeout(Duration.ofSeconds(5)).build())
                .build();

        LettuceClientConfiguration.LettuceSslClientConfigurationBuilder builder =
                LettuceClientConfiguration.builder()
                        .clientResources(resources)
                        .clientOptions(options)
                        .commandTimeout(Duration.ofSeconds(10))
                        .useSsl(); // ★ TLS

        return new LettuceConnectionFactory(conf, builder.build());
    }
}
