package com.nhnacademy.iot_service.redis.sub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.iot_service.controller.SensorWebSocketController;
import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.connection.Message;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisSubscriberTest {

    @Mock
    ObjectMapper objectMapper;

    @Mock
    SensorWebSocketController webSocketController;

    @InjectMocks
    RedisSubscriber redisSubscriber;

    @Test
    @DisplayName("정상적인 메시지 수신 시 WebSocket으로 전송")
    void onMessage_Success() throws Exception {
        String jsonString = "{\"sensorName\":\"sensor1\",\"status\":\"ON\"}";
        byte[] testMessageBody = jsonString.getBytes();
        Message message = new DefaultMessage(testMessageBody, new byte[0]);

        SensorResult testSensorResult = new SensorResult("sensor1", "room1", "ON", List.of());

        // Mock 설정: byte[]와 SensorResult.class 타입 명시
        when(objectMapper.readValue(
                any(byte[].class),
                eq(SensorResult.class)
        )).thenReturn(testSensorResult);

        // When
        redisSubscriber.onMessage(message, null);

        // Then
        verify(webSocketController, times(1)).sendSensorResult(testSensorResult);
    }

    @Test
    @DisplayName("잘못된 메시지 수신 시 예외 처리 및 로깅")
    void onMessage_DeserializationError() throws Exception {
        String jsonString = "{\"sensorName\":\"sensor1\",\"status\":\"ON\"}";
        byte[] testMessageBody = jsonString.getBytes();

        Message message = new DefaultMessage(testMessageBody, new byte[0]);
        when(objectMapper.readValue(any(byte[].class), eq(SensorResult.class)))
                .thenThrow(new IOException("Invalid JSON"));

        redisSubscriber.onMessage(message, null);

        verify(webSocketController, never()).sendSensorResult(any());
    }
}