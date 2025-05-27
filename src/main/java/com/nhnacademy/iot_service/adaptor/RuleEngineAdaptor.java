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

@FeignClient(
        name = "rule-engine-service",
        url = "${rule-engine-service.url}",
        path = "/api/v1/rule-engine"
)
public interface RuleEngineAdaptor {

    @PostMapping("/trigger")
    ResponseEntity<List<RuleEvaluationResult>> executeTriggeredRules(
            @RequestParam String eventType,
            @RequestParam(required = false, defaultValue = "{}") String eventParams,
            @RequestBody Map<String, Object> facts
    );

    @PostMapping("/manual/{ruleNo}")
    ResponseEntity<RuleEvaluationResult> executeRule(
            @PathVariable Long ruleNo,
            @RequestBody Map<String, Object> facts
    );
}