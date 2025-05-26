package com.nhnacademy.iot_service.dto.sensor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 센서 정보를 클라이언트에 응답할 때 사용하는 DTO 클래스입니다.
 * 서버가 센서 조회 또는 등록 결과를 반환할 때 사용됩니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorResponse {
    Long sensorNo;
    String sensorName;
    String sensorType;
    Boolean sensorStatus;
    String location;
}
