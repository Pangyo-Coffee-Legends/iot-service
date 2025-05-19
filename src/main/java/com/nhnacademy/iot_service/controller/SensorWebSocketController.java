package com.nhnacademy.iot_service.controller;

import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SensorWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sensor.subscribe")
    public void subscribe() {
        // 클라이언트 구독 처리
    }

    public void sendSensorResult(SensorResult result) {
        log.debug("Sending to WebSocket : {}", result);
        messagingTemplate.convertAndSend("/topic/sensor-result", result);
    }
}
