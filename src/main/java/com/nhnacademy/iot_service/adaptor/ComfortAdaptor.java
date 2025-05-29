package com.nhnacademy.iot_service.adaptor;

import com.nhnacademy.iot_service.dto.engine.RuleEvaluationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 쾌적도(rule-engine-comfort) 서비스와의 통신을 위한 Feign 클라이언트 어댑터입니다.
 * <p>
 * /api/v1/comfort 경로 하위의 쾌적도 관련 API를 호출합니다.
 * </p>
 */
@FeignClient(
        name = "rule-engine-comfort",
        url = "${rule-engine-service.url}",
        path = "/api/v1/comfort"
)
public interface ComfortAdaptor {

    /**
     * 쾌적도 스케줄 결과를 조회합니다.
     *
     * @return 쾌적도 규칙 평가 결과 리스트를 포함하는 ResponseEntity
     */
    @GetMapping("/scheduled-result")
    ResponseEntity<List<RuleEvaluationResult>> getScheduledResult();
}