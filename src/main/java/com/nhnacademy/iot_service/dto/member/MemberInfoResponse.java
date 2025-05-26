package com.nhnacademy.iot_service.dto.member;

import lombok.Value;

@Value
public class MemberInfoResponse {
    Long no;
    String name;
    String email;
    String phoneNumber;
}
