package com.nhnacademy.iot_service.service.impl;

import com.nhnacademy.iot_service.adaptor.ComfortAdaptor;
import com.nhnacademy.iot_service.adaptor.MemberAdaptor;
import com.nhnacademy.iot_service.adaptor.RuleEngineAdaptor;
import com.nhnacademy.iot_service.auth.MemberThreadLocal;
import com.nhnacademy.iot_service.domain.Role;
import com.nhnacademy.iot_service.domain.Sensor;
import com.nhnacademy.iot_service.domain.SensorMemberMapping;
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
import com.nhnacademy.iot_service.service.SensorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SensorServiceImpl implements SensorService {
    private final SensorRepository sensorRepository;
    private final RuleEngineAdaptor ruleEngineAdaptor;
    private final ComfortAdaptor comfortAdaptor;
    private final RedisPublisher redisPublisher;
    private final RoleRepository roleRepository;
    private final MemberAdaptor memberAdaptor;
    private final SensorMbMappingRepository mbMappingRepository;

    @Override
    public SensorResponse registerSensor(SensorRegisterRequest request) {
        String email = MemberThreadLocal.getMemberEmail();

        ResponseEntity<MemberResponse> response = memberAdaptor.getMemberByEmail(email);

        if (response == null || response.getBody() == null) {
            log.error("register sensor member not found");
            throw new MemberNotFoundException(email);
        }

        String roleName = response.getBody().getRoleName();

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(roleName));

        if (!role.getRoleName().equals("ROLE_ADMIN")) {
            log.error("권한이 맞지 않습니다.");
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }

        Sensor sensor = sensorRepository.save(
                new Sensor (
                        request.getSensorName(),
                        request.getSensorType(),
                        request.getSensorStatus(),
                        request.getLocation()
                )
        );

        try {
            mbMappingRepository.save(
                    SensorMemberMapping.ofNewSensorMemberMapping(
                            sensor,
                            response.getBody().getNo()
                    )
            );
        } catch (DataAccessException e) {
            log.error("register sensor mapping failed");
            throw new SensorPersistException("sensor member mapping failed : " + e);
        }

        return toSensorResponse(sensor);
    }

    @Override
    public SensorResponse updateSensor(Long sensorNo, SensorUpdateRequest request) {
        String email = MemberThreadLocal.getMemberEmail();

        ResponseEntity<MemberResponse> response = memberAdaptor.getMemberByEmail(email);

        if (response == null || response.getBody() == null) {
            log.error("update sensor member not found");
            throw new MemberNotFoundException(email);
        }

        Sensor sensor = sensorRepository.findById(sensorNo)
                .orElseThrow(() -> new SensorNotFoundException(sensorNo));

        sensor.update(
                request.getSensorName(),
                request.getSensorType()
        );

        Sensor save = sensorRepository.save(sensor);
        log.debug("sensor update : {}", save);

        return toSensorResponse(save);
    }

    @Override
    public void deleteSensor(Long sensorNo) {
        String email = MemberThreadLocal.getMemberEmail();

        ResponseEntity<MemberResponse> response = memberAdaptor.getMemberByEmail(email);

        if (response == null || response.getBody() == null) {
            log.error("delete sensor member not found");
            throw new MemberNotFoundException(email);
        }

        if (!sensorRepository.existsById(sensorNo)) {
            log.error("sensor no Not Found : {}", sensorNo);
            throw new SensorNotFoundException(sensorNo);
        }

        sensorRepository.deleteById(sensorNo);
        log.debug("sensor delete success");
    }

    @Override
    public SensorResponse getSensor(Long sensorNo) {
        String email = MemberThreadLocal.getMemberEmail();

        ResponseEntity<MemberResponse> response = memberAdaptor.getMemberByEmail(email);

        if (response == null || response.getBody() == null) {
            log.error("get sensor member not found");
            throw new MemberNotFoundException(email);
        }

        return sensorRepository.findById(sensorNo)
                .map(this::toSensorResponse)
                .orElseThrow(() -> new SensorNotFoundException(sensorNo));
    }

    @Override
    public List<SensorResponse> getSensorByLocation(String location) {
        String email = MemberThreadLocal.getMemberEmail();

        ResponseEntity<MemberResponse> response = memberAdaptor.getMemberByEmail(email);

        if (response == null || response.getBody() == null) {
            log.error("get sensor by location member not found");
            throw new MemberNotFoundException(email);
        }

        List<Sensor> sensorList = sensorRepository.findByLocation(location);

        return sensorList.stream()
                .map(this::toSensorResponse)
                .toList();
    }

    @Override
    public SensorResult getSensorStatus(Long sensorNo) {
        Sensor origin = sensorRepository.findById(sensorNo)
                .orElseThrow(() -> new SensorNotFoundException(sensorNo));

        Map<String, Object> facts = getFacts(origin);

        ResponseEntity<List<RuleEvaluationResult>> response = ruleEngineAdaptor
                .executeTriggeredRules("AI_DATA_RECEIVED", "{\"source\":\"AI\"}", facts);

        List<RuleEvaluationResult> body = response.getBody();

        boolean isOn = body != null &&
                body.stream().anyMatch(RuleEvaluationResult::isSuccess);

        return new SensorResult(
                origin.getSensorName(),
                origin.getLocation(),
                isOn ? "on" : "off",
                response.getBody()
        );
    }

    /**
     * 센서 정보와 ComfortAdaptor에서 받은 스케줄된 룰 평가 결과를 조합하여 facts 맵을 생성합니다.
     *
     * <p>
     * 이 메서드는 다음 단계로 동작합니다:
     * <ol>
     *   <li>ComfortAdaptor를 통해 주기적으로 수집된 룰 평가 결과를 조회합니다.</li>
     *   <li>센서 엔티티에서 필드값을 추출합니다.</li>
     *   <li>추출된 센서 정보와 룰 평가 결과를 {@code Map<String, Object>} 형태로 병합합니다.</li>
     * </ol>
     * 생성된 facts 맵은 룰 엔진 평가에 사용됩니다.
     * </p>
     *
     * @param origin 데이터베이스에서 조회한 센서 엔티티. {@code null}이 아니어야 합니다.
     * @return 룰 엔진에 전달할 facts 맵. 키: "sensorName", "location", "sensorType",
     *         "sensorStatus", "scheduledResults"를 포함합니다.
     */
    private Map<String, Object> getFacts(Sensor origin) {
        ResponseEntity<List<RuleEvaluationResult>> scheduleResults =
                comfortAdaptor.getScheduledResult();

        List<RuleEvaluationResult> results = scheduleResults.getBody();

        return Map.of(
                "sensorName", origin.getSensorName(),
                "location", origin.getLocation(),
                "sensorType", origin.getSensorType(),
                "sensorStatus", origin.getSensorState(),
                "scheduledResults", results // ComfortAdaptor 결과 추가
        );
    }

    @Override
    public List<SensorResult> getSensorStatusByLocation(Long sensorNo) {
        // 1. 기준 센서 조회 및 위치 확인
        Sensor origin = sensorRepository.findById(sensorNo)
                .orElseThrow(() -> new SensorNotFoundException(sensorNo));
        String targetLocation = origin.getLocation();

        // 2. 동일 위치의 모든 센서 조회
        List<Sensor> locationSensors = sensorRepository.findByLocation(targetLocation);
        if (locationSensors.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 위치 기반 팩트 생성
        Map<String, Object> facts = buildLocationFacts(locationSensors);

        // 4. 룰 엔진 실행
        ResponseEntity<List<RuleEvaluationResult>> response = ruleEngineAdaptor
                .executeTriggeredRules("LOCATION_ANALYSIS", "{\"scope\":\"location\"}", facts);

        // 5. 디바이스 상태 추출 (Map<String, Boolean>)
        Map<String, Boolean> deviceStates = extractDeviceStates(response.getBody());

        // 6. 센서별 결과 매핑 (기존 룰 결과 전체 전달)
        List<SensorResult> results = mapToSensorResults(locationSensors, deviceStates, response.getBody());

        // 7. Redis로 각 SensorResult 발행 (추가된 부분)
        results.forEach(redisPublisher::publishSensorData);

        return results;
    }

    // 위치 기반 팩트 생성 메서드
    private Map<String, Object> buildLocationFacts(List<Sensor> sensors) {
        Map<String, Boolean> deviceStatusMap = sensors.stream()
                .collect(Collectors.toMap(
                        Sensor::getSensorType,
                        Sensor::getSensorState
                ));

        return Map.of(
                "location", sensors.get(0).getLocation(),
                "devices", deviceStatusMap,
                "deviceCount", sensors.size()
        );
    }

    // 룰 실행 결과에서 디바이스 상태 추출
    private Map<String, Boolean> extractDeviceStates(List<RuleEvaluationResult> ruleResults) {
        Map<String, Boolean> deviceStates = new HashMap<>();
        if (ruleResults != null) {
            ruleResults.forEach(ruleResult ->
                    ruleResult.getExecutedActions().forEach(action -> {
                        Map<String, Object> output = (Map<String, Object>) action.getOutput();
                        output.forEach((key, value) -> {
                            if (value instanceof Boolean boolValue) {
                                deviceStates.put(key.toLowerCase(), boolValue);
                            }
                        });
                    })
            );
        }
        return deviceStates;
    }

    // 센서 결과 매핑 (기존 룰 결과 전체 전달)
    private List<SensorResult> mapToSensorResults(
            List<Sensor> sensors,
            Map<String, Boolean> deviceStates,
            List<RuleEvaluationResult> ruleResults
    ) {
        return sensors.stream()
                .map(sensor -> {
                    String type = sensor.getSensorType().toLowerCase();
                    boolean isActive = deviceStates.getOrDefault(type, false);
                    return new SensorResult(
                            sensor.getSensorName(),
                            sensor.getLocation(),
                            isActive ? "ON" : "OFF",
                            ruleResults // 원본 룰 평가 결과 전체 전달
                    );
                })
                .toList();
    }

    private SensorResponse toSensorResponse(Sensor sensor) {
        return new SensorResponse(
                sensor.getSensorNo(),
                sensor.getSensorName(),
                sensor.getSensorType(),
                sensor.getSensorState(),
                sensor.getLocation()
        );
    }
}
