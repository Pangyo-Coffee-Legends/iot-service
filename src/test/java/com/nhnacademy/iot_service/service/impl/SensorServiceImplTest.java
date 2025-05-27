package com.nhnacademy.iot_service.service.impl;

import com.nhnacademy.iot_service.adaptor.ComfortAdaptor;
import com.nhnacademy.iot_service.adaptor.MemberAdaptor;
import com.nhnacademy.iot_service.adaptor.RuleEngineAdaptor;
import com.nhnacademy.iot_service.auth.MemberThreadLocal;
import com.nhnacademy.iot_service.domain.Role;
import com.nhnacademy.iot_service.domain.Sensor;
import com.nhnacademy.iot_service.domain.SensorMemberMapping;
import com.nhnacademy.iot_service.dto.action.ActionResult;
import com.nhnacademy.iot_service.dto.engine.RuleEvaluationResult;
import com.nhnacademy.iot_service.dto.member.MemberResponse;
import com.nhnacademy.iot_service.dto.sensor.SensorRegisterRequest;
import com.nhnacademy.iot_service.dto.sensor.SensorResponse;
import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import com.nhnacademy.iot_service.dto.sensor.SensorUpdateRequest;
import com.nhnacademy.iot_service.exception.*;
import com.nhnacademy.iot_service.redis.pub.RedisPublisher;
import com.nhnacademy.iot_service.repository.RoleRepository;
import com.nhnacademy.iot_service.repository.SensorMbMappingRepository;
import com.nhnacademy.iot_service.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

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
    SensorMbMappingRepository mbMappingRepository;

    @Mock
    SensorRepository sensorRepository;

    @Mock
    MemberAdaptor memberAdaptor;

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

    private String testEmail = "test@example.com";
    private Long testMemberNo = 1L;
    private Long testSensorNo = 100L;
    private String testLocation = "Office";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        aircon = new Sensor("회의실 에어컨", "aircon", true, "회의실");
        heater = new Sensor("회의실 히터", "heater", false, "회의실");
        ventilator = new Sensor("회의실 환풍기", "ventilator", true, "회의실");
    }

    @Test
    @DisplayName("센서 등록 성공")
    void registerSensor_Success() {
        MemberThreadLocal.setMemberEmail(testEmail);

        SensorRegisterRequest request = new SensorRegisterRequest("센서A", "aircon", true, "회의실");
        MemberResponse memberResponse = new MemberResponse(testMemberNo, "ROLE_ADMIN","user", testEmail, "abcd1234!", "010-1234-5678");
        Role role = Role.ofNewRole("ROLE_ADMIN", "관리자 입니다.");
        Sensor sensor = new Sensor("센서A", "aircon", true, "회의실");

        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(ResponseEntity.ok(memberResponse));
        when(roleRepository.findByRoleName("ROLE_ADMIN")).thenReturn(Optional.of(role));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(sensor);

        SensorResponse response = sensorService.registerSensor(request);

        assertNotNull(response);
        assertEquals("센서A", response.getSensorName());
        verify(mbMappingRepository, times(1)).save(any(SensorMemberMapping.class));
    }

    @Test
    @DisplayName("registerSensor 실패 - 회원 없음")
    void registerSensor_memberNotFound() {
        SensorRegisterRequest request = new SensorRegisterRequest("센서A", "aircon", true, "회의실");
        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(null);

        assertThrows(MemberNotFoundException.class, () -> sensorService.registerSensor(request));
    }

    @Test
    @DisplayName("registerSensor 실패 - Role 없음")
    void registerSensor_roleNotFound() {
        MemberThreadLocal.setMemberEmail(testEmail);

        SensorRegisterRequest request = new SensorRegisterRequest("센서A", "aircon", true, "회의실");
        MemberResponse memberResponse = new MemberResponse(testMemberNo, "ROLE_ADMIN","user", testEmail, "abcd1234!", "010-1234-5678");

        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(ResponseEntity.ok(memberResponse));
        when(roleRepository.findByRoleName("ROLE_ADMIN")).thenReturn(java.util.Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> sensorService.registerSensor(request));
    }

    @Test
    @DisplayName("registerSensor 실패 - 권한 불일치")
    void registerSensor_accessDenied() {
        MemberThreadLocal.setMemberEmail(testEmail);

        SensorRegisterRequest request = new SensorRegisterRequest("센서A", "aircon", true, "회의실");
        MemberResponse memberResponse = new MemberResponse(testMemberNo, "ROLE_USER","user", testEmail, "abcd1234!", "010-1234-5678");
        Role role = Role.ofNewRole("ROLE_USER", "사용자 입니다.");

        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(ResponseEntity.ok(memberResponse));
        when(roleRepository.findByRoleName("ROLE_USER")).thenReturn(java.util.Optional.of(role));

        assertThrows(AccessDeniedException.class, () -> sensorService.registerSensor(request));
    }

    @Test
    @DisplayName("registerSensor 실패 - 센서-멤버 매핑 저장 실패")
    void registerSensor_sensorMappingFail() {
        MemberThreadLocal.setMemberEmail(testEmail);

        SensorRegisterRequest request = new SensorRegisterRequest("센서A", "aircon", true, "회의실");
        MemberResponse memberResponse = new MemberResponse(testMemberNo, "ROLE_ADMIN","user", testEmail, "abcd1234!", "010-1234-5678");
        Role role = Role.ofNewRole("ROLE_ADMIN", "관리자 입니다.");
        Sensor sensor = new Sensor("센서A", "aircon", true, "회의실");

        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(ResponseEntity.ok(memberResponse));
        when(roleRepository.findByRoleName("ROLE_ADMIN")).thenReturn(Optional.of(role));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(sensor);
        doThrow(new org.springframework.dao.DataAccessException("fail"){}).when(mbMappingRepository).save(any(SensorMemberMapping.class));

        assertThrows(SensorPersistException.class, () -> sensorService.registerSensor(request));
    }

    @Test
    @DisplayName("updateSensor 성공")
    void updateSensor_success() {
        MemberThreadLocal.setMemberEmail(testEmail);

        SensorUpdateRequest request = new SensorUpdateRequest("NewName", "NewType");
        MemberResponse memberResponse = new MemberResponse(testMemberNo, "ROLE_ADMIN","user", testEmail, "abcd1234!", "010-1234-5678");
        Sensor sensor = new Sensor("OldName", "OldType", true, "회의실");

        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(ResponseEntity.ok(memberResponse));
        when(sensorRepository.findById(testSensorNo)).thenReturn(java.util.Optional.of(sensor));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(sensor);

        SensorResponse response = sensorService.updateSensor(testSensorNo, request);

        assertNotNull(response);
        assertEquals("NewName", response.getSensorName());
        assertEquals("NewType", response.getSensorType());
        verify(sensorRepository).save(sensor);
    }

    @Test
    @DisplayName("updateSensor 실패 - 회원 없음")
    void updateSensor_memberNotFound() {
        SensorUpdateRequest request = new SensorUpdateRequest("NewName", "NewType");
        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(null);

        assertThrows(MemberNotFoundException.class, () -> sensorService.updateSensor(testSensorNo, request));
    }

    @Test
    @DisplayName("updateSensor 실패 - 센서 없음")
    void updateSensor_sensorNotFound() {
        MemberThreadLocal.setMemberEmail(testEmail);

        SensorUpdateRequest request = new SensorUpdateRequest("NewName", "NewType");
        MemberResponse memberResponse = new MemberResponse(testMemberNo, "ROLE_ADMIN","user", testEmail, "abcd1234!", "010-1234-5678");
        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(ResponseEntity.ok(memberResponse));
        when(sensorRepository.findById(testSensorNo)).thenReturn(java.util.Optional.empty());

        assertThrows(SensorNotFoundException.class, () -> sensorService.updateSensor(testSensorNo, request));
    }

    @Test
    @DisplayName("deleteSensor 성공")
    void deleteSensor_success() {
        MemberThreadLocal.setMemberEmail(testEmail);

        MemberResponse memberResponse = new MemberResponse(testMemberNo, "ROLE_ADMIN","user", testEmail, "abcd1234!", "010-1234-5678");
        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(ResponseEntity.ok(memberResponse));
        when(sensorRepository.existsById(testSensorNo)).thenReturn(true);

        assertDoesNotThrow(() -> sensorService.deleteSensor(testSensorNo));

        verify(sensorRepository).deleteById(testSensorNo);
    }

    @Test
    @DisplayName("deleteSensor 실패 - 회원 없음")
    void deleteSensor_memberNotFound() {
        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(null);

        assertThrows(MemberNotFoundException.class, () -> sensorService.deleteSensor(testSensorNo));
    }

    @Test
    @DisplayName("deleteSensor 실패 - 센서 없음")
    void deleteSensor_sensorNotFound() {
        MemberThreadLocal.setMemberEmail(testEmail);

        MemberResponse memberResponse = new MemberResponse(testMemberNo, "ROLE_ADMIN","user", testEmail, "abcd1234!", "010-1234-5678");
        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(ResponseEntity.ok(memberResponse));
        when(sensorRepository.existsById(testSensorNo)).thenReturn(false);

        assertThrows(SensorNotFoundException.class, () -> sensorService.deleteSensor(testSensorNo));
    }

    @Test
    @DisplayName("getSensor 성공")
    void getSensor_success() {
        MemberThreadLocal.setMemberEmail(testEmail);

        MemberResponse memberResponse = new MemberResponse(testMemberNo, "ROLE_ADMIN","user", testEmail, "abcd1234!", "010-1234-5678");
        Sensor sensor = new Sensor("센서A", "aircon", true, "회의실");

        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(ResponseEntity.ok(memberResponse));
        when(sensorRepository.findById(testSensorNo)).thenReturn(java.util.Optional.of(sensor));

        SensorResponse response = sensorService.getSensor(testSensorNo);

        assertNotNull(response);
        assertEquals("센서A", response.getSensorName());
    }

    @Test
    @DisplayName("getSensor 실패 - 회원 없음")
    void getSensor_memberNotFound() {
        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(null);

        assertThrows(MemberNotFoundException.class, () -> sensorService.getSensor(testSensorNo));
    }

    @Test
    @DisplayName("getSensor 실패 - 센서 없음")
    void getSensor_sensorNotFound() {
        MemberThreadLocal.setMemberEmail(testEmail);

        MemberResponse memberResponse = new MemberResponse(testMemberNo, "ROLE_ADMIN","user", testEmail, "abcd1234!", "010-1234-5678");
        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(ResponseEntity.ok(memberResponse));
        when(sensorRepository.findById(testSensorNo)).thenReturn(java.util.Optional.empty());

        assertThrows(SensorNotFoundException.class, () -> sensorService.getSensor(testSensorNo));
    }

    @Test
    @DisplayName("getSensorByLocation 성공")
    void getSensorByLocation_success() {
        MemberThreadLocal.setMemberEmail(testEmail);

        MemberResponse memberResponse = new MemberResponse(testMemberNo, "ROLE_ADMIN","user", testEmail, "abcd1234!", "010-1234-5678");
        List<Sensor> sensors = List.of(
                new Sensor("센서A", "aircon", true, testLocation),
                new Sensor("센서B", "heater", false, testLocation)
        );

        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(ResponseEntity.ok(memberResponse));
        when(sensorRepository.findByLocation(testLocation)).thenReturn(sensors);

        List<SensorResponse> responses = sensorService.getSensorByLocation(testLocation);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("센서A", responses.get(0).getSensorName());
        assertEquals("센서B", responses.get(1).getSensorName());
    }

    @Test
    @DisplayName("getSensorByLocation 실패 - 회원 없음")
    void getSensorByLocation_memberNotFound() {
        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(null);

        assertThrows(MemberNotFoundException.class, () -> sensorService.getSensorByLocation(testLocation));
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
    @DisplayName("getSensorStatus - results 가 비어 있을 때")
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
}