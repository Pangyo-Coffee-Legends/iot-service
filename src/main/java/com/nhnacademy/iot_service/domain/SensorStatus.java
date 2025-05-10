package com.nhnacademy.iot_service.domain;

/**
 * 센서의 작동 상태를 표현하는 열거형(Enum)입니다.
 * true/false 대신 의미 있는 이름으로 상태를 표현할 수 있습니다.
 */
public enum SensorStatus {
    ON,     // 정상 작동 중
    OFF    // 작동 중지 또는 오류
}