package com.nhnacademy.iot_service.adaptor;


import com.nhnacademy.iot_service.dto.engine.RuleEvaluationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 룰 엔진(rule-engine-service)과의 통신을 위한 Feign 클라이언트 어댑터입니다.
 * <p>
 * /api/v1/rule-engine 경로 하위의 룰 실행 관련 API를 호출합니다.
 * </p>
 */
@FeignClient(
        name = "rule-engine-service",
        url = "${rule-engine-service.url}",
        path = "/api/v1/rule-engine"
)
public interface RuleEngineAdaptor {

    /**
     * 이벤트 유형과 이벤트 파라미터, 팩트 정보를 기반으로 트리거된 룰을 실행합니다.
     *
     * @param eventType   이벤트 유형
     * @param eventParams 이벤트 파라미터(기본값: 빈 JSON)
     * @param facts       룰 평가에 사용할 팩트 정보(맵)
     * @return 룰 평가 결과 리스트를 포함한 ResponseEntity (HTTP 200 OK)
     */
    @PostMapping("/trigger")
    ResponseEntity<List<RuleEvaluationResult>> executeTriggeredRules(
            @RequestParam String eventType,
            @RequestParam(required = false, defaultValue = "{}") String eventParams,
            @RequestBody Map<String, Object> facts
    );

    /**
     * 특정 룰 번호와 팩트 정보를 기반으로 룰을 수동 실행합니다.
     *
     * @param ruleNo 실행할 룰의 고유 번호
     * @param facts  룰 평가에 사용할 팩트 정보(맵)
     * @return 룰 평가 결과를 포함한 ResponseEntity (HTTP 200 OK)
     */
    @PostMapping("/manual/{ruleNo}")
    ResponseEntity<RuleEvaluationResult> executeRule(
            @PathVariable Long ruleNo,
            @RequestBody Map<String, Object> facts
    );
}