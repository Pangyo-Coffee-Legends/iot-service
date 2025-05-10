package com.nhnacademy.iot_service.dto.sensor;

import lombok.Value;

/**
 * 센서 등록 요청을 처리하기 위한 DTO 클래스입니다.
 * 클라이언트가 서버에 센서를 생성 요청할 때 사용하는 데이터 구조입니다.
 */
@Value
public class SensorRegisterRequest {
    String sensorName;
    String sensorType;
    Boolean sensorStatus;
    String location;

}
