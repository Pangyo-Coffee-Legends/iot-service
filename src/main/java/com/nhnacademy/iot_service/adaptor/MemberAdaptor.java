package com.nhnacademy.iot_service.adaptor;

import com.nhnacademy.iot_service.dto.member.MemberInfoResponse;
import com.nhnacademy.iot_service.dto.member.MemberResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 회원(member-service) 서비스와의 통신을 위한 Feign 클라이언트 어댑터입니다.
 * <p>
 * /api/v1/members 경로 하위의 회원 관련 API를 호출합니다.
 * </p>
 */
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