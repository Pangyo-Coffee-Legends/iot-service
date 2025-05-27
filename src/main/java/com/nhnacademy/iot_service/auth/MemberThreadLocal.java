package com.nhnacademy.iot_service.auth;

public class MemberThreadLocal {
    private static final ThreadLocal<String> memberEmailLocal = new ThreadLocal<>();

    private MemberThreadLocal() { throw new IllegalStateException("Utility class"); }

    public static String getMemberEmail() { return memberEmailLocal.get(); }

    public static void setMemberEmail(String email) { memberEmailLocal.set(email); }

    public static void removedMemberEmail() { memberEmailLocal.remove(); }
}
