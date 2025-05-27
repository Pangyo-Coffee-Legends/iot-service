package com.nhnacademy.iot_service.adaptor;

import com.nhnacademy.iot_service.dto.member.MemberInfoResponse;
import com.nhnacademy.iot_service.dto.member.MemberResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "member-service",
        url = "${member-service.url}",
        path = "/api/v1/members"
)
public interface MemberAdaptor {

    /**
     * 이메일(회원 고유 번호)로 특정 회원의 상세 정보를 조회합니다.
     *
     * @param mbEmail 조회할 회원의 이메일(고유 식별자, PathVariable)
     * @return 해당 회원의 상세 정보가 담긴 ResponseEntity (HTTP 200 OK)
     */
    @GetMapping("/email/{mbEmail}")
    ResponseEntity<MemberResponse> getMemberByEmail(@PathVariable String mbEmail);

    /**
     * 전체 회원의 요약 정보 목록을 조회합니다.
     *
     * @return 회원 요약 정보 리스트가 담긴 ResponseEntity (HTTP 200 OK)
     */
    @GetMapping("/info-list")
    ResponseEntity<List<MemberInfoResponse>> getMemberInfoList();
}
