package com.nhnacademy.iot_service.controller;

import com.nhnacademy.iot_service.domain.Sensor;
import com.nhnacademy.iot_service.dto.SensorRequest;
import com.nhnacademy.iot_service.dto.SensorResponse;
import com.nhnacademy.iot_service.repository.SensorRepository;
import com.nhnacademy.iot_service.service.SensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 센서 관련 요청을 처리하는 REST 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/v1/sensors")
public class SensorController {

    private final SensorService sensorService;
    private final SensorRepository sensorRepository;

    public SensorController(SensorService sensorService, SensorRepository sensorRepository) {
        this.sensorService = sensorService;
        this.sensorRepository = sensorRepository;
    }

    /**
     * 모든 센서 정보를 조회합니다.
     *
     * @return 센서 리스트
     */
    @GetMapping
    public ResponseEntity<List<SensorResponse>> getAllSensors() {
        List<Sensor> sensors = sensorService.getAllSensors();
        List<SensorResponse> dtos = sensors.stream()
                .map(SensorResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * 센서 상태를 ON/OFF로 변경합니다.
     *
     * @param sensorNo 센서 번호
     * @param status   true: ON, false: OFF
     * @return 업데이트된 센서 정보
     */
    @PutMapping("/{sensorNo}/status")
    public ResponseEntity<SensorResponse> updateSensorStatus(@PathVariable Long sensorNo,
                                                                @RequestParam("status") boolean status) {
        Sensor updated = sensorService.updateSensorStatus(sensorNo, status);
        return ResponseEntity.ok(SensorResponse.from(updated));
    }

    /**
     * 센서를 새로 등록합니다.
     *
     * @param request 센서 생성 요청 DTO
     * @return 생성된 센서 정보
     */
    @PostMapping
    public ResponseEntity<SensorResponse> createSensor(@RequestBody SensorRequest request) {
        Sensor sensor = new Sensor(
                request.getSensorName(),
                request.getSensorType(),
                request.getSensorStatus(),
                request.getLocation()
        );
        Sensor saved = sensorRepository.save(sensor);
        return ResponseEntity.ok(SensorResponse.from(saved));
    }
}
