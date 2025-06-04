package com.nhnacademy.iot_service.controller;

import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * 센서 데이터의 WebSocket 통신을 처리하는 컨트롤러입니다.
 * <p>
 * 클라이언트의 구독 요청을 처리하고, 센서 결과를 WebSocket을 통해 브로드캐스트합니다.
 * </p>
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class SensorWebSocketController {

    /**
     * STOMP 메시지 전송을 위한 템플릿입니다.
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 클라이언트의 센서 데이터 구독 요청을 처리합니다.
     * <p>
     * "/app/sensor.subscribe" 경로로 들어오는 메시지를 처리합니다.
     * 현재는 별도의 로직 없이 구독만 처리합니다.
     * </p>
     */
    @MessageMapping("/sensor.subscribe")
    public void subscribe() {
        // 클라이언트 구독 처리
    }

    /**
     * 센서 결과를 WebSocket을 통해 구독 중인 모든 클라이언트에게 전송합니다.
     *
     * @param result 전송할 센서 결과 데이터
     */
    public void sendSensorResult(SensorResult result) {
        log.debug("Sending to WebSocket : {}", result);
        messagingTemplate.convertAndSend("/topic/sensor-result", result);
    }
}