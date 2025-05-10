package com.nhnacademy.iot_service.service.impl;

import com.nhnacademy.iot_service.adaptor.ComfortAdaptor;
import com.nhnacademy.iot_service.adaptor.RuleEngineAdaptor;
import com.nhnacademy.iot_service.domain.Sensor;
import com.nhnacademy.iot_service.dto.action.ActionResult;
import com.nhnacademy.iot_service.dto.engine.RuleEvaluationResult;
import com.nhnacademy.iot_service.dto.sensor.SensorRegisterRequest;
import com.nhnacademy.iot_service.dto.sensor.SensorResponse;
import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import com.nhnacademy.iot_service.dto.sensor.SensorUpdateRequest;
import com.nhnacademy.iot_service.exception.SensorNotFoundException;
import com.nhnacademy.iot_service.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class SensorServiceImplTest {

    @Mock
    SensorRepository sensorRepository;

    @Mock
    RuleEngineAdaptor ruleEngineAdaptor;

    @Mock
    ComfortAdaptor comfortAdaptor;

    @InjectMocks
    SensorServiceImpl sensorService;

    private Sensor aircon;
    private Sensor heater;
    private Sensor ventilator;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        aircon = new Sensor("회의실 에어컨", "aircon", true, "회의실");
        heater = new Sensor("회의실 히터", "heater", false, "회의실");
        ventilator = new Sensor("회의실 환풍기", "ventilator", true, "회의실");
    }

    @Test
    @DisplayName("registerSensor 성공")
    void registerSensor_Success() {
        // 준비
        SensorRegisterRequest request = new SensorRegisterRequest(
                "Sensor1", "TypeA", true, "Room1"
        );
        Sensor savedSensor = new Sensor(
                request.getSensorName(),
                request.getSensorType(),
                request.getSensorStatus(),
                request.getLocation()
        );

        when(sensorRepository.save(any(Sensor.class))).thenReturn(savedSensor);

        // 실행
        SensorResponse result = sensorService.registerSensor(request);

        // 검증
        verify(sensorRepository).save(any(Sensor.class));
        assertEquals("Sensor1", result.getSensorName());
        assertEquals("Room1", result.getLocation());
    }

    @Test
    @DisplayName("updateSensor 성공")
    void updateSensor_Success() {
        Long sensorNo = 1L;
        SensorUpdateRequest request = new SensorUpdateRequest("NewSensor", "TypeB");

        Sensor existingSensor = new Sensor(
                "OldSensor",
                "TypeA",
                true,
                "A class"
        );

        when(sensorRepository.findById(sensorNo)).thenReturn(Optional.of(existingSensor));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(existingSensor);

        SensorResponse result = sensorService.updateSensor(sensorNo, request);

        verify(sensorRepository).findById(sensorNo);
        verify(sensorRepository).save(any(Sensor.class));
        assertEquals("NewSensor", result.getSensorName());
        assertEquals("TypeB", result.getSensorType());
    }

    @Test
    @DisplayName("deleteSensor 성공")
    void deleteSensor_Success() {
        Long sensorNo = 1L;
        when(sensorRepository.existsById(sensorNo)).thenReturn(true);

        sensorService.deleteSensor(sensorNo);

        verify(sensorRepository).deleteById(sensorNo);
    }

    @Test
    @DisplayName("deleteSensor - sensor not found")
    void deleteSensor_NotFound() {
        Long sensorNo = 1L;
        when(sensorRepository.existsById(sensorNo)).thenReturn(false);

        assertThrows(SensorNotFoundException.class, () -> sensorService.deleteSensor(sensorNo));
    }

    @Test
    @DisplayName("getSensorStatus 성공")
    void getSensorStatus_Success() {
        Long sensorNo = 1L;
        Sensor sensor = new Sensor(
                "Sensor1",
                "TypeA",
                true,
                "Room1"
        );
        when(sensorRepository.findById(sensorNo)).thenReturn(Optional.of(sensor));

        List<RuleEvaluationResult> scheduledResults = List.of(
                new RuleEvaluationResult(1L, "ruleA", true)
        );
        when(comfortAdaptor.getScheduledResult())
                .thenReturn(ResponseEntity.ok(scheduledResults));

        List<RuleEvaluationResult> ruleResults = List.of(
                new RuleEvaluationResult(2L, "ruleB", true)
        );
        when(ruleEngineAdaptor.executeTriggeredRules(
                eq("AI_DATA_RECEIVED"),
                eq("{\"source\":\"AI\"}"),
                any())
        ).thenReturn(ResponseEntity.ok(ruleResults));

        SensorResult result = sensorService.getSensorStatus(sensorNo);

        assertEquals("Sensor1", result.getSensorName());
        assertEquals("on", result.getStatus());
        assertEquals(ruleResults, result.getRuleResults());
    }

    @Test
    @DisplayName("getSensorStatus - results 가 비어있을 때")
    void getSensorStatus_EmptyRuleResults() {
        Long sensorNo = 1L;
        Sensor sensor = new Sensor(
                "empty",
                "type",
                true,
                "a"
        );

        when(sensorRepository.findById(sensorNo)).thenReturn(Optional.of(sensor));
        when(comfortAdaptor.getScheduledResult()).thenReturn(ResponseEntity.ok(List.of()));
        when(ruleEngineAdaptor.executeTriggeredRules(any(), any(), any()))
                .thenReturn(ResponseEntity.ok(null));

        SensorResult result = sensorService.getSensorStatus(sensorNo);

        // 검증: 결과가 null 인 경우 "off" 상태
        assertEquals("off", result.getStatus());
        assertNull(result.getRuleResults());
    }

    @Test
    @DisplayName("센서를 찾을 수 없습니다.")
    void getSensor_NotFound() {
        Long sensorNo = 1L;
        when(sensorRepository.findById(sensorNo)).thenReturn(Optional.empty());

        assertThrows(SensorNotFoundException.class, () -> sensorService.getSensor(sensorNo));
    }

    @Test
    @DisplayName("회의실 센서 상태가 룰 엔진 결과에 따라 올바르게 매핑 - 성공")
    void getSensorStatusByLocation_returnsCorrectStatus() {
        Long sensorNo = 1L;
        when(sensorRepository.findById(sensorNo)).thenReturn(Optional.of(aircon));
        when(sensorRepository.findByLocation("회의실"))
                .thenReturn(List.of(aircon, heater, ventilator));

        // 룰 엔진 결과 세팅
        Map<String, Object> airconOutput = new HashMap<>();
        airconOutput.put("aircon", true);
        Map<String, Object> heaterOutput = new HashMap<>();
        heaterOutput.put("heater", false);
        Map<String, Object> ventilatorOutput = new HashMap<>();
        ventilatorOutput.put("ventilator", true);

        ActionResult airconAction = mock(ActionResult.class);
        when(airconAction.getOutput()).thenReturn(airconOutput);
        ActionResult heaterAction = mock(ActionResult.class);
        when(heaterAction.getOutput()).thenReturn(heaterOutput);
        ActionResult ventilatorAction = mock(ActionResult.class);
        when(ventilatorAction.getOutput()).thenReturn(ventilatorOutput);

        RuleEvaluationResult rule1 = mock(RuleEvaluationResult.class);
        when(rule1.getExecutedActions()).thenReturn(List.of(airconAction));
        RuleEvaluationResult rule2 = mock(RuleEvaluationResult.class);
        when(rule2.getExecutedActions()).thenReturn(List.of(heaterAction));
        RuleEvaluationResult rule3 = mock(RuleEvaluationResult.class);
        when(rule3.getExecutedActions()).thenReturn(List.of(ventilatorAction));

        List<RuleEvaluationResult> ruleResults = List.of(rule1, rule2, rule3);

        when(ruleEngineAdaptor.executeTriggeredRules(
                eq("LOCATION_ANALYSIS"),
                anyString(),
                any()
        )).thenReturn(ResponseEntity.ok(ruleResults));

        List<SensorResult> results = sensorService.getSensorStatusByLocation(sensorNo);

        assertEquals(3, results.size());

        Map<String, SensorResult> resultMap = new HashMap<>();
        results.forEach(r -> resultMap.put(r.getSensorName(), r));

        assertEquals("ON", resultMap.get("회의실 에어컨").getStatus());
        assertEquals("OFF", resultMap.get("회의실 히터").getStatus());
        assertEquals("ON", resultMap.get("회의실 환풍기").getStatus());

        // ruleEvaluationResults가 그대로 전달되는지 검증
        assertEquals(ruleResults, resultMap.get("회의실 에어컨").getRuleResults());
        assertEquals(ruleResults, resultMap.get("회의실 히터").getRuleResults());
        assertEquals(ruleResults, resultMap.get("회의실 환풍기").getRuleResults());
    }

    @Test
    @DisplayName("위치에 센서가 없으면 빈 리스트를 반환")
    void getSensorStatusByLocation_noSensorsInLocation_returnsEmptyList() {
        Long sensorNo = 1L;
        when(sensorRepository.findById(sensorNo)).thenReturn(Optional.of(aircon));
        when(sensorRepository.findByLocation("회의실")).thenReturn(Collections.emptyList());

        List<SensorResult> results = sensorService.getSensorStatusByLocation(sensorNo);

        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("존재하지 않는 센서 번호로 조회하면 예외가 발생")
    void getSensorStatusByLocation_sensorNotFound_throwsException() {
        Long sensorNo = 999L;
        when(sensorRepository.findById(sensorNo)).thenReturn(Optional.empty());

        assertThrows(SensorNotFoundException.class, () ->
                sensorService.getSensorStatusByLocation(sensorNo));
    }
}