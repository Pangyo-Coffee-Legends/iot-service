package com.nhnacademy.iot_service.redis.sub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.iot_service.controller.SensorWebSocketController;
import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Redis Pub/Sub 메시지를 수신(구독)하는 컴포넌트입니다.
 * <p>
 * Redis에서 센서 결과 메시지를 수신하면, 이를 역직렬화하여
 * WebSocket을 통해 클라이언트로 전달합니다.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    /**
     * JSON 역직렬화에 사용되는 ObjectMapper입니다.
     */
    private final ObjectMapper objectMapper;

    /**
     * 센서 결과를 WebSocket으로 전송하는 컨트롤러입니다.
     */
    private final SensorWebSocketController webSocketController;

    /**
     * Redis로부터 메시지를 수신하면 호출되는 메서드입니다.
     * <ul>
     *     <li>수신한 메시지를 JSON 문자열로 변환</li>
     *     <li>SensorResult 객체로 역직렬화</li>
     *     <li>WebSocket을 통해 결과를 클라이언트에 전송</li>
     *     <li>역직렬화 실패 시 에러 로그 출력</li>
     * </ul>
     *
     * @param message Redis에서 수신한 메시지
     * @param pattern 구독한 패턴(채널) 정보
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String rawJson = new String(message.getBody(), StandardCharsets.UTF_8);
            log.debug("Received message from Redis: {}", rawJson);

            // 1. JSON → SensorResult 역직렬화
            SensorResult result = objectMapper.readValue(rawJson, SensorResult.class);
            log.debug("Deserialized SensorResult: {}", result);

            // 2. WebSocket으로 결과 전송
            webSocketController.sendSensorResult(result);
        } catch (IOException e) {
            String rawMessage = new String(message.getBody(), StandardCharsets.UTF_8);
            log.error("Failed to deserialize message: {}", rawMessage, e);
        }
    }
}