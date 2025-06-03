package com.nhnacademy.iot_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 메시지 브로커 구성을 위한 설정 클래스입니다.
 * <p>
 * STOMP 프로토콜을 사용한 WebSocket 엔드포인트 및 메시지 브로커의 설정을 담당합니다.
 * </p>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * STOMP WebSocket 엔드포인트를 등록합니다.
     * <p>
     * 클라이언트는 "/ws-sensor" 엔드포인트를 통해 WebSocket에 연결할 수 있습니다.
     * 모든 도메인에서의 접속을 허용합니다.
     * </p>
     *
     * @param registry STOMP 엔드포인트 레지스트리
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-sensor").setAllowedOriginPatterns("http://localhost:10253");
    }

    /**
     * 메시지 브로커의 동작을 구성합니다.
     * <ul>
     *     <li>"/topic"으로 시작하는 경로는 내장 메시지 브로커가 처리합니다.</li>
     *     <li>"/app"으로 시작하는 경로는 애플리케이션(컨트롤러)로 라우팅됩니다.</li>
     *     <li>메시지 순서 보장을 위해 publish order를 유지합니다.</li>
     * </ul>
     *
     * @param registry 메시지 브로커 레지스트리
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setPreservePublishOrder(true); // 메시지 순서 보장
    }
}