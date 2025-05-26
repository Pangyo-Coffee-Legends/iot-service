package com.nhnacademy.iot_service.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super("Role not found No : %s".formatted(message));
    }
}
