package com.nhnacademy.iot_service.auth;

/**
 * 회원 이메일을 ThreadLocal에 저장하고 관리하는 유틸리티 클래스입니다.
 * <p>
 * 각 스레드(예: 각 HTTP 요청)별로 독립적으로 회원 이메일 정보를 저장하고 조회할 수 있도록 지원합니다.
 * </p>
 * <p>
 * 이 클래스는 인스턴스화할 수 없습니다.
 * </p>
 */
public class MemberThreadLocal {

    /**
     * 스레드별 회원 이메일 정보를 저장하는 ThreadLocal 변수입니다.
     */
    private static final ThreadLocal<String> memberEmailLocal = new ThreadLocal<>();

    /**
     * 인스턴스화 방지를 위한 private 생성자입니다.
     * 호출 시 예외를 발생시킵니다.
     */
    private MemberThreadLocal() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 현재 스레드에 저장된 회원 이메일을 반환합니다.
     *
     * @return 현재 스레드에 저장된 회원 이메일, 없으면 null
     */
    public static String getMemberEmail() {
        return memberEmailLocal.get();
    }

    /**
     * 현재 스레드에 회원 이메일을 저장합니다.
     *
     * @param email 저장할 회원 이메일
     */
    public static void setMemberEmail(String email) {
        memberEmailLocal.set(email);
    }

    /**
     * 현재 스레드에 저장된 회원 이메일 정보를 삭제합니다.
     */
    public static void removedMemberEmail() {
        memberEmailLocal.remove();
    }
}