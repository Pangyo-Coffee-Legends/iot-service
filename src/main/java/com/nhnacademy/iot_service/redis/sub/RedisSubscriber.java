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

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SensorWebSocketController webSocketController;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            SensorResult result = objectMapper.readValue(message.getBody(), SensorResult.class);
            webSocketController.sendSensorResult(result);
        } catch (IOException e) {
            log.error("redis subscriber error : ", e);
        }
    }
}
