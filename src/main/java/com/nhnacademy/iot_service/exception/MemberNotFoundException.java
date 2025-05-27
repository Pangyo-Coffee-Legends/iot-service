package com.nhnacademy.iot_service.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String message) {
        super("Member not found email : %s".formatted(message));
    }
}
