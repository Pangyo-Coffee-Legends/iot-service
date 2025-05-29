package com.nhnacademy.iot_service.exception;

/**
 * 센서 정보 저장(Persist) 중 예외가 발생했을 때 사용하는 예외 클래스입니다.
 * <p>
 * 센서 등록, 수정, 삭제 등 데이터베이스에 센서 정보를 저장하는 과정에서 오류가 발생할 경우 사용합니다.
 * </p>
 */
public class SensorPersistException extends RuntimeException {

    /**
     * 센서 정보 저장 중 발생한 예외 메시지와 함께 예외를 생성합니다.
     *
     * @param message 예외 원인에 대한 상세 메시지
     */
    public SensorPersistException(String message) {
        super(message);
    }
}