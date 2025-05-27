package com.nhnacademy.iot_service.exception;

public class SensorNotFoundException extends RuntimeException {
    public SensorNotFoundException(Long message) {
        super("sensor not found No : %d".formatted(message));
    }
}
