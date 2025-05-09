package com.nhnacademy.iot_service.controller;

import com.nhnacademy.iot_service.dto.SensorCreateRequestDto;
import com.nhnacademy.iot_service.dto.SensorResponseDto;
import com.nhnacademy.iot_service.service.SensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 센서 관련 HTTP 요청을 처리하는 REST 컨트롤러입니다.
 * 센서 등록 및 조회 기능을 제공합니다.
 */
@RestController
@RequestMapping("/sensors")
public class SensorController {

    private final SensorService sensorService;

    /**
     * SensorController 생성자입니다.
     *
     * @param sensorService 센서 서비스 계층 의존성
     */
    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    /**
     * 센서를 등록합니다.
     *
     * @param requestDto 클라이언트가 전송한 센서 등록 요청 데이터
     * @return 등록된 센서 정보
     */
    @PostMapping
    public ResponseEntity<SensorResponseDto> createSensor(@RequestBody SensorCreateRequestDto requestDto) {
        SensorResponseDto responseDto = sensorService.createSensor(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 센서를 고유 번호로 조회합니다.
     *
     * @param sensorNo 조회할 센서의 고유 번호
     * @return 해당 센서의 정보
     */
    @GetMapping("/{sensorNo}")
    public ResponseEntity<SensorResponseDto> getSensor(@PathVariable Long sensorNo) {
        SensorResponseDto responseDto = sensorService.getSensor(sensorNo);
        return ResponseEntity.ok(responseDto);
    }
}
