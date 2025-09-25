package com.example.steam.core.config.load_test_config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Profile("load")
@Configuration
@EnableWebSocketMessageBroker
public class StompDebugConfig implements WebSocketMessageBrokerConfigurer {
    @Override public void configureClientInboundChannel(ChannelRegistration r) {
        r.interceptors(new ChannelInterceptor() {
            @Override public Message<?> preSend(Message<?> m, MessageChannel c) {
                var h = StompHeaderAccessor.wrap(m);
                System.out.println("INBOUND  " + h.getCommand() + " " + h.getDestination());
                return m;
            }
        });
    }
    @Override public void configureClientOutboundChannel(ChannelRegistration r) {
        r.interceptors(new ChannelInterceptor() {
            @Override public Message<?> preSend(Message<?> m, MessageChannel c) {
                var h = StompHeaderAccessor.wrap(m);
                System.out.println("OUTBOUND " + h.getCommand() + " " + h.getDestination());
                return m;
            }
        });
    }
}
