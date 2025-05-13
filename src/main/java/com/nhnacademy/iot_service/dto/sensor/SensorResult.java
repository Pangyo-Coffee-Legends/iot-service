package com.nhnacademy.iot_service.dto.sensor;

import com.nhnacademy.iot_service.dto.engine.RuleEvaluationResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorResult {
    String sensorName;
    String location;
    String status; // "ON" 또는 "OFF"
    List<RuleEvaluationResult> ruleResults;
}
