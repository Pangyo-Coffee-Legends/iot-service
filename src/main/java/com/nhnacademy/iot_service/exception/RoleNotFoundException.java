package com.nhnacademy.iot_service.exception;

/**
 * 역할(Role) 정보를 찾을 수 없을 때 발생하는 예외입니다.
 * <p>
 * 주로 주어진 역할 번호로 역할을 조회했을 때 결과가 없을 경우 사용합니다.
 * </p>
 */
public class RoleNotFoundException extends RuntimeException {

    /**
     * 주어진 역할 번호로 역할을 찾을 수 없을 때 예외를 생성합니다.
     *
     * @param message 역할 번호 등 추가 정보
     */
    public RoleNotFoundException(String message) {
        super("Role not found No : %s".formatted(message));
    }
}
