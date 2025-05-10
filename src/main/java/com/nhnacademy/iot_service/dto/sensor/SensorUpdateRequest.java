package com.nhnacademy.iot_service.dto.sensor;

import lombok.Value;

@Value
public class SensorUpdateRequest {
    String sensorName;
    String sensorType;
}
