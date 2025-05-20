package com.nhnacademy.iot_service.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(Long message) {
        super("Role not found No : %d".formatted(message));
    }
}
