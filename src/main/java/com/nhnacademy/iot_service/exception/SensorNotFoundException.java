package com.nhnacademy.iot_service.exception;

/**
 * 센서 정보를 찾을 수 없을 때 발생하는 예외입니다.
 * <p>
 * 주로 주어진 센서 번호로 센서를 조회했을 때 결과가 없을 경우 사용합니다.
 * </p>
 */
public class SensorNotFoundException extends RuntimeException {

    /**
     * 주어진 센서 번호로 센서를 찾을 수 없을 때 예외를 생성합니다.
     *
     * @param message 센서 번호
     */
    public SensorNotFoundException(Long message) {
        super("sensor not found No : %d".formatted(message));
    }
}