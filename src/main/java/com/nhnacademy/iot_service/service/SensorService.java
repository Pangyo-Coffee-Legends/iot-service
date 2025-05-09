package com.nhnacademy.iot_service.service;

import com.nhnacademy.iot_service.domain.Sensor;

import java.util.List;

public interface SensorService {
    void processSensorData(Sensor sensor);  // RuleEngine 결과 기반 제어
    List<Sensor> getAllSensors();
    Sensor updateSensorStatus(Long sensorNo, boolean status);
}
