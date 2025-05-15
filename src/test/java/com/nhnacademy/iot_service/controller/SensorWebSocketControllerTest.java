package com.nhnacademy.iot_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.iot_service.dto.action.ActionResult;
import com.nhnacademy.iot_service.dto.condition.ConditionResult;
import com.nhnacademy.iot_service.dto.engine.RuleEvaluationResult;
import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
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

    @Test
    @DisplayName("실제 실행되는 걸 가정")
    void sendSensorResult_simulation() throws Exception {
        // 1. 테스트용 데이터 생성
        LocalDateTime now = LocalDateTime.now();
        SensorResult sensorResult = getSensorResult(now);

        // 2. 메서드 실행
        sensorWebSocketController.sendSensorResult(sensorResult);

        // 3. 검증: 메시지가 정확한 경로와 내용으로 전송되었는지
        verify(messagingTemplate, times(1))
                .convertAndSend("/topic/sensor-result", sensorResult);

        // 4. JSON 직렬화 검증을 위한 추가 로깅
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        System.out.println("전송된 데이터 구조:");
        System.out.println(objectMapper.writeValueAsString(sensorResult));
    }

    @NotNull
    private static SensorResult getSensorResult(LocalDateTime now) {
        List<ConditionResult> conditions = List.of(
                new ConditionResult(1L, "temperature", "GT", "25", true)
        );

        List<ActionResult> actions = List.of(
                new ActionResult(100L, true, "COMFORT_NOTIFICATION", "온도가 높습니다!", null, now)
        );

        List<RuleEvaluationResult> ruleResults = List.of(
                new RuleEvaluationResult(1L, "온도 규칙", true, conditions, actions, "규칙 통과", now)
        );

        return new SensorResult(
                "Sensor-01",
                "Room-101",
                "ON",
                ruleResults
        );
    }
}