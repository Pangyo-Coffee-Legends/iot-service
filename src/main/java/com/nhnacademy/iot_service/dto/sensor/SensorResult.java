package com.nhnacademy.iot_service.dto.sensor;

import com.nhnacademy.iot_service.dto.engine.RuleEvaluationResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 센서의 상태 및 규칙 평가 결과를 담는 DTO입니다.
 * <p>
 * 센서 이름, 위치, 상태(ON/OFF), 그리고 규칙 평가 결과 리스트를 포함합니다.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorResult {

    /**
     * 센서 이름
     */
    String sensorName;

    /**
     * 센서 위치
     */
    String location;

    /**
     * 센서 상태 ("ON" 또는 "OFF")
     */
    String status;

    /**
     * 센서에 적용된 규칙들의 평가 결과 리스트
     */
    List<RuleEvaluationResult> ruleResults;
}