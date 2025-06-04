package com.nhnacademy.iot_service.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketConfigTest {

    @Mock
    StompEndpointRegistry stompEndpointRegistry;

    @Mock
    StompWebSocketEndpointRegistration stompEndpointRegistration;

    @Mock
    MessageBrokerRegistry messageBrokerRegistry;

    @InjectMocks
    WebSocketConfig webSocketConfig;

    @Test
    @DisplayName("STOMP 엔드포인트 /ws-sensor 가 등록되고 CORS가 허용된다")
    void registerStompEndpoints() {
        when(stompEndpointRegistry.addEndpoint("/ws-sensor")).thenReturn(stompEndpointRegistration);
        when(stompEndpointRegistration.setAllowedOriginPatterns("*")).thenReturn(stompEndpointRegistration);

        webSocketConfig.registerStompEndpoints(stompEndpointRegistry);

        verify(stompEndpointRegistry).addEndpoint("/ws-sensor");
        verify(stompEndpointRegistration).setAllowedOriginPatterns("*");
    }

    @Test
    @DisplayName("메시지 브로커는 /topic 을 구독하며 애플리케이션 목적지 접두사는 /app 이다")
    void configureMessageBroker() {
        // When
        webSocketConfig.configureMessageBroker(messageBrokerRegistry);

        // Then
        verify(messageBrokerRegistry).enableSimpleBroker("/topic");
        verify(messageBrokerRegistry).setApplicationDestinationPrefixes("/app");
    }
}