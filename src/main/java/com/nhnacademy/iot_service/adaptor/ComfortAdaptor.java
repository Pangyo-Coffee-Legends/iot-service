package com.nhnacademy.iot_service.adaptor;

import com.nhnacademy.iot_service.dto.engine.RuleEvaluationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(
        name = "rule-engine-comfort",
        url = "${rule-engine-service.url}",
        path = "/api/v1/comfort"
)
public interface ComfortAdaptor {

    @GetMapping("/scheduled-result")
    ResponseEntity<List<RuleEvaluationResult>> getScheduledResult();
}
