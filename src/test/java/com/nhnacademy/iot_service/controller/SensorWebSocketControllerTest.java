package com.nhnacademy.iot_service.controller;

import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SensorWebSocketControllerTest {

    @Mock
    SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    SensorWebSocketController sensorWebSocketController;

    @Test
    @DisplayName("클라이언트 구독 처리 메서드는 빈 메서드로 유지")
    void subscribe_ShouldDoNothing() {
        sensorWebSocketController.subscribe();
        // 메서드가 비어있으므로 아무 동작도 하지 않음
    }

    @Test
    @DisplayName("sendSensorResult 메서드는 메시지를 /topic/sensor-result 로 전송")
    void sendSensorResult_ShouldSendMessage() {
        SensorResult result = new SensorResult("sensor1", "room1", "ON", List.of());
        sensorWebSocketController.sendSensorResult(result);
        verify(messagingTemplate, times(1)).convertAndSend("/topic/sensor-result", result);
    }
}