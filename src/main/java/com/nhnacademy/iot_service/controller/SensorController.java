package com.nhnacademy.iot_service.controller;

import com.nhnacademy.iot_service.dto.sensor.SensorRegisterRequest;
import com.nhnacademy.iot_service.dto.sensor.SensorResponse;
import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import com.nhnacademy.iot_service.dto.sensor.SensorUpdateRequest;
import com.nhnacademy.iot_service.redis.pub.RedisPublisher;
import com.nhnacademy.iot_service.service.SensorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 센서 관련 REST API를 제공하는 컨트롤러입니다.
 * <p>
 * 센서 등록, 단일 센서 조회, 위치별 센서 목록 조회 기능을 제공합니다.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/sensors")
@RequiredArgsConstructor
public class SensorController {

    /**
     * 센서 비즈니스 로직을 처리하는 서비스입니다.
     */
    private final SensorService sensorService;
    private final RedisPublisher redisPublisher;

    /**
     * 센서를 등록합니다.
     *
     * @param request 센서 등록 요청 데이터
     * @return 등록된 센서 정보와 함께 200 OK 응답
     */
    @PostMapping
    public ResponseEntity<SensorResponse> registerSensor(@RequestBody SensorRegisterRequest request) {
        SensorResponse response = sensorService.registerSensor(request);
        log.debug("registerSensor : {}", response);

        return ResponseEntity
                .ok(response);
    }

    @PostMapping("/send")
    public void sendSensorResult(@RequestBody SensorResult result) {
        redisPublisher.publishSensorData(result);
    }

    /**
     * 센서 고유 번호로 센서 정보를 조회합니다.
     *
     * @param sensorNo 센서 고유 번호
     * @return 센서 정보와 함께 200 OK 응답
     */
    @GetMapping("/{sensorNo}")
    public ResponseEntity<SensorResponse> getSensor(@PathVariable("sensorNo") Long sensorNo) {
        SensorResponse response = sensorService.getSensor(sensorNo);
        log.debug("getSensor : {}", response);
        return ResponseEntity
                .ok(response);
    }

    @GetMapping("/places")
    public ResponseEntity<List<String>> getLocations() {
        List<String> locations = sensorService.getAllSensorLocations();

        log.debug("getLocations : {}", locations);

        return ResponseEntity.ok(locations);
    }

    /**
     * 센서 위치(장소)로 센서 목록을 조회합니다.
     *
     * @param sensorPlace 센서 위치(장소) (URL 인코딩된 문자열)
     * @return 해당 위치의 센서 정보 리스트와 함께 200 OK 응답
     */
    @GetMapping("/place/{sensorPlace}")
    public ResponseEntity<List<SensorResponse>> getSensors(@PathVariable("sensorPlace") String sensorPlace) {
        String decodedPlace = URLDecoder.decode(sensorPlace, StandardCharsets.UTF_8);
        List<SensorResponse> responseList = sensorService.getSensorByLocation(decodedPlace);
        log.debug("getSensors : {}", responseList);

        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/{sensorNo}")
    public ResponseEntity<SensorResponse> updateSensor(@PathVariable("sensorNo") Long sensorNo,
                                                       @RequestBody SensorUpdateRequest request) {
        SensorResponse response = sensorService.updateSensor(sensorNo, request);
        log.debug("updateSensor : {}", response);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(response);
    }

    @DeleteMapping("/{sensorNo}")
    public ResponseEntity<SensorResponse> deleteSensor(@PathVariable("sensorNo") Long sensorNo) {
        sensorService.deleteSensor(sensorNo);
        log.debug("deleteSensor : {}", sensorNo);
        return ResponseEntity.noContent().build();
    }
}
