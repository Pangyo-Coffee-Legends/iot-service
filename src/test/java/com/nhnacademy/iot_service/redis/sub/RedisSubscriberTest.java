package com.nhnacademy.iot_service.redis.sub;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.iot_service.controller.SensorWebSocketController;
import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisSubscriberTest {

    @Mock
    ObjectMapper objectMapper;

    @Mock
    SensorWebSocketController webSocketController;

    @Mock
    private Message message;

    RedisSubscriber redisSubscriber;
    ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setUp() {
        redisSubscriber = new RedisSubscriber(objectMapper, webSocketController);

        Logger logger = (Logger) LoggerFactory.getLogger(RedisSubscriber.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
    }

    @Test
    @DisplayName("onMessage: 유효한 메시지 수신 시 WebSocket으로 전송")
    void onMessage_ValidMessage_ShouldSendToWebSocket() throws Exception {
        String json = "{\"sensorName\":\"sensor1\",\"location\":\"room1\"}";
        SensorResult expectedResult = new SensorResult("sensor1", "room1", "ON", null);

        when(message.getBody()).thenReturn(json.getBytes(StandardCharsets.UTF_8));
        when(objectMapper.readValue(json, SensorResult.class)).thenReturn(expectedResult);

        redisSubscriber.onMessage(message, null);

        verify(webSocketController).sendSensorResult(expectedResult);
    }

    @Test
    @DisplayName("onMessage: 잘못된 메시지 수신 시 에러 로깅")
    void onMessage_InvalidMessage_ShouldLogError() throws Exception {
        String invalidJson = "invalid_json";
        byte[] invalidData = invalidJson.getBytes(StandardCharsets.UTF_8);

        when(message.getBody()).thenReturn(invalidData);

        // JsonParseException (구체 적인 예외) 사용
        doThrow(new JsonParseException(null, "Deserialization failed"))
                .when(objectMapper)
                .readValue(invalidJson, SensorResult.class);

        redisSubscriber.onMessage(message, null);

        // Then: 로그 검증
        assertTrue(logAppender.list.stream()
                .anyMatch(event ->
                        event.getFormattedMessage().contains("Failed to deserialize message")
                ));
    }
}