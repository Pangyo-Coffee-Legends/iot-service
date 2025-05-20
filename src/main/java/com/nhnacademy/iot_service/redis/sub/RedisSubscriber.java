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

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SensorWebSocketController webSocketController;

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
