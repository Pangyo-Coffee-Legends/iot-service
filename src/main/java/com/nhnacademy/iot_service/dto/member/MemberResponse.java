package com.nhnacademy.iot_service.dto.member;

import lombok.Value;

/**
 * 회원의 상세 정보를 담는 응답 DTO입니다.
 * <p>
 * 회원 고유 번호, 역할명, 이름, 이메일, 비밀번호, 전화번호 정보를 포함합니다.
 * </p>
 */
@Value
public class MemberResponse {

    /**
     * 회원 고유 번호 (PK)
     */
    Long no;

    /**
     * 회원 역할명 (예: ADMIN, USER 등)
     */
    String roleName;

    /**
     * 회원 이름
     */
    String name;

    /**
     * 회원 이메일 주소
     */
    String email;

    /**
     * 회원 비밀번호
     */
    String password;

    /**
     * 회원 전화번호
     */
    String phoneNumber;
}