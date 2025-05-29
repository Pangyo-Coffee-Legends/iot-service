package com.nhnacademy.iot_service.dto.member;

import lombok.Value;

/**
 * 회원의 기본 정보를 담는 응답 DTO입니다.
 * <p>
 * 회원 고유 번호, 이름, 이메일, 전화번호 정보를 제공합니다.
 * </p>
 */
@Value
public class MemberInfoResponse {

    /**
     * 회원 고유 번호 (PK)
     */
    Long no;

    /**
     * 회원 이름
     */
    String name;

    /**
     * 회원 이메일 주소
     */
    String email;

    /**
     * 회원 전화번호
     */
    String phoneNumber;
}