package com.nhnacademy.iot_service.controller;

import com.nhnacademy.iot_service.dto.sensor.SensorRegisterRequest;
import com.nhnacademy.iot_service.dto.sensor.SensorResponse;
import com.nhnacademy.iot_service.service.SensorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 센서 관련 요청을 처리하는 REST 컨트롤러입니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    @PostMapping
    public ResponseEntity<SensorResponse> registerSensor(@RequestBody SensorRegisterRequest request) {
        SensorResponse response = sensorService.registerSensor(request);
        log.debug("registerSensor : {}", response);

        return ResponseEntity
                .ok(response);
    }

    @GetMapping("/{sensorNo}")
    public ResponseEntity<SensorResponse> getSensor(@PathVariable("sensorNo") Long sensorNo) {
        SensorResponse response = sensorService.getSensor(sensorNo);
        log.debug("getSensor : {}", response);
        return ResponseEntity
                .ok(response);
    }

    @GetMapping("/place/{sensorPlace}")
    public ResponseEntity<List<SensorResponse>> getSensors(@PathVariable("sensorPlace") String sensorPlace) {
        List<SensorResponse> responseList = sensorService.getSensorByLocation(sensorPlace);
        log.debug("getSensors : {}", responseList);

        return ResponseEntity.ok(responseList);
    }
}
