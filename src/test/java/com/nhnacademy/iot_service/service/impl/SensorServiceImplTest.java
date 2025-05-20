package com.nhnacademy.iot_service.service.impl;

import com.nhnacademy.iot_service.adaptor.ComfortAdaptor;
import com.nhnacademy.iot_service.adaptor.RuleEngineAdaptor;
import com.nhnacademy.iot_service.domain.Role;
import com.nhnacademy.iot_service.domain.Sensor;
import com.nhnacademy.iot_service.dto.action.ActionResult;
import com.nhnacademy.iot_service.dto.engine.RuleEvaluationResult;
import com.nhnacademy.iot_service.dto.sensor.SensorRegisterRequest;
import com.nhnacademy.iot_service.dto.sensor.SensorResponse;
import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import com.nhnacademy.iot_service.dto.sensor.SensorUpdateRequest;
import com.nhnacademy.iot_service.exception.RoleNotFoundException;
import com.nhnacademy.iot_service.exception.SensorNotFoundException;
import com.nhnacademy.iot_service.redis.pub.RedisPublisher;
import com.nhnacademy.iot_service.repository.RoleRepository;
import com.nhnacademy.iot_service.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class SensorServiceImplTest {

    @Mock
    RoleRepository roleRepository;

    @Mock
    SensorRepository sensorRepository;

    @Mock
    RuleEngineAdaptor ruleEngineAdaptor;

    @Mock
    ComfortAdaptor comfortAdaptor;

    @InjectMocks
    SensorServiceImpl sensorService;

    @Mock
    RedisPublisher redisPublisher;

    private Sensor aircon;
    private Sensor heater;
    private Sensor ventilator;
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        role = Role.ofNewRole("ADMIN", "관리자");
        setField(role, "roleNo", 2L);

        aircon = new Sensor(role, "회의실 에어컨", "aircon", true, "회의실");
        heater = new Sensor(role, "회의실 히터", "heater", false, "회의실");
        ventilator = new Sensor(role, "회의실 환풍기", "ventilator", true, "회의실");
    }

    @Test
    @DisplayName("센서 등록 시 Role 연관관계가 올바르게 저장됨")
    void registerSensor_SavesRoleRelationship() {
        Long roleNo = 10L;
        Role testRole = Role.ofNewRole("USER", "일반 사용자");
        setField(testRole, "roleNo", roleNo); // 강제 ID 세팅

        SensorRegisterRequest request = new SensorRegisterRequest(
                roleNo, "센서A", "aircon", true, "회의실"
        );
        Sensor savedSensor = new Sensor(testRole, "센서A", "aircon", true, "회의실");

        when(roleRepository.findById(roleNo)).thenReturn(Optional.of(testRole));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(savedSensor);

        SensorResponse result = sensorService.registerSensor(request);

        verify(roleRepository).findById(roleNo);
        verify(sensorRepository).save(any(Sensor.class));
        assertEquals(roleNo, result.getRoleNo());
        assertEquals("센서A", result.getSensorName());
    }

    @Test
    @DisplayName("센서 등록 시 Role이 없으면 예외 발생")
    void registerSensor_RoleNotFound_ThrowsException() {
        Long roleNo = 999L;
        SensorRegisterRequest request = new SensorRegisterRequest(
                roleNo, "센서B", "heater", true, "회의실"
        );
        when(roleRepository.findById(roleNo)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> sensorService.registerSensor(request));
    }

    @Test
    @DisplayName("센서 조회 시 Role 정보가 포함됨")
    void getSensor_ReturnsRoleInfo() {
        Long sensorNo = 1L;
        Long roleNo = 10L;
        Role testRole = Role.ofNewRole("USER", "일반 사용자");
        setField(testRole, "roleNo", roleNo);

        Sensor sensor = new Sensor(testRole, "센서C", "ventilator", true, "회의실");
        when(sensorRepository.findById(sensorNo)).thenReturn(Optional.of(sensor));

        SensorResponse result = sensorService.getSensor(sensorNo);

        assertEquals(roleNo, result.getRoleNo());
        assertEquals("센서C", result.getSensorName());
    }

    @Test
    @DisplayName("registerSensor 성공")
    void registerSensor_Success() {
        SensorRegisterRequest request = new SensorRegisterRequest(
                role.getRoleNo(), "Sensor1", "TypeA", true, "Room1"
        );
        Sensor savedSensor = new Sensor(
                role,
                request.getSensorName(),
                request.getSensorType(),
                request.getSensorStatus(),
                request.getLocation()
        );

        when(roleRepository.findById(any())).thenReturn(Optional.ofNullable(role));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(savedSensor);

        SensorResponse result = sensorService.registerSensor(request);

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
                role,
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
    @DisplayName("센서 업데이트 시 Role은 변경되지 않는다")
    void updateSensor_DoesNotChangeRole() {
        Long sensorNo = 1L;
        Role originalRole = Role.ofNewRole("ADMIN", "관리자");
        setField(originalRole, "roleNo", 1L);

        Sensor sensor = new Sensor(originalRole, "센서D", "aircon", true, "회의실");
        when(sensorRepository.findById(sensorNo)).thenReturn(Optional.of(sensor));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(sensor);

        SensorUpdateRequest updateRequest = new SensorUpdateRequest("새센서", "heater");
        SensorResponse result = sensorService.updateSensor(sensorNo, updateRequest);

        assertEquals(1L, result.getRoleNo());
        assertEquals("새센서", result.getSensorName());
        assertEquals("heater", result.getSensorType());
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
    @DisplayName("센서 삭제 시 연관 Role은 삭제되지 않는다")
    void deleteSensor_DoesNotDeleteRole() {
        Long sensorNo = 1L;
        when(sensorRepository.existsById(sensorNo)).thenReturn(true);

        sensorService.deleteSensor(sensorNo);

        verify(sensorRepository).deleteById(sensorNo);
        verifyNoInteractions(roleRepository); // RoleRepository는 호출되지 않아야 함
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
                role,
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
                role,
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

        // 1. RuleEvaluationResult 생성 방식 수정
        Map<String, Object> airconOutput = Map.of("aircon", true);
        Map<String, Object> heaterOutput = Map.of("heater", false);
        Map<String, Object> ventilatorOutput = Map.of("ventilator", true);

        ActionResult airconAction = new ActionResult(
                1001L, true, "DEVICE_CONTROL",
                "에어컨 제어 성공", airconOutput, LocalDateTime.now()
        );

        ActionResult heaterAction = new ActionResult(
                1002L, false, "DEVICE_CONTROL",
                "히터 제어 실패", heaterOutput, LocalDateTime.now()
        );

        ActionResult ventilatorAction = new ActionResult(
                1003L, true, "DEVICE_CONTROL",
                "환풍기 제어 성공", ventilatorOutput, LocalDateTime.now()
        );

        // 3. RuleEvaluationResult Mock 생성 (모든 액션 포함)
        RuleEvaluationResult ruleResult = mock(RuleEvaluationResult.class);
        when(ruleResult.getExecutedActions())
                .thenReturn(List.of(airconAction, heaterAction, ventilatorAction));

        when(ruleEngineAdaptor.executeTriggeredRules(
                eq("LOCATION_ANALYSIS"),
                anyString(),
                any(Map.class)
        )).thenReturn(ResponseEntity.ok(List.of(ruleResult)));

        // When
        List<SensorResult> results = sensorService.getSensorStatusByLocation(sensorNo);

        // Then
        assertEquals(3, results.size(), "3개의 센서 결과가 반환되어야 함");

        // 4. 센서 타입별 상태 검증
        Map<String, SensorResult> resultMap = new HashMap<>();
        results.forEach(r -> resultMap.put(r.getSensorName().split(" ")[1], r));

        assertAll(
                () -> assertEquals("ON", resultMap.get("에어컨").getStatus(),
                        "에어컨은 ON 상태여야 함"),
                () -> assertEquals("OFF", resultMap.get("히터").getStatus(),
                        "히터는 OFF 상태여야 함"),
                () -> assertEquals("ON", resultMap.get("환풍기").getStatus(),
                        "환풍기는 ON 상태여야 함")
        );

        // 5. Redis 발행 횟수 검증
        verify(redisPublisher, times(3)).publishSensorData(any(SensorResult.class));

        // 6. 룰 결과 전달 검증
        results.forEach(r ->
                assertSame(ruleResult.getExecutedActions(),
                        r.getRuleResults().get(0).getExecutedActions(),
                        "모든 센서 결과에 원본 룰 실행 결과가 포함되어야 함")
        );
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

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}