package com.nhnacademy.iot_service.adaptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.iot_service.dto.action.ActionResult;
import com.nhnacademy.iot_service.dto.condition.ConditionResult;
import com.nhnacademy.iot_service.dto.engine.RuleEvaluationResult;
import com.nhnacademy.iot_service.repository.SensorRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
        "rule-engine-service.url=http://localhost:${wiremock.server.port}"
})
@ActiveProfiles("test")
class ComfortAdaptorTest {

    @MockitoBean
    SensorRepository sensorRepository;

    @Autowired
    ComfortAdaptor comfortAdaptor;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("스케줄 반환 성공")
    void getScheduledResult_returnsMockedResponse() throws Exception {
        RuleEvaluationResult mockResult = getRuleEvaluationResult();

        List<RuleEvaluationResult> results = List.of(mockResult);

        // 2. WireMock으로 응답 미리 등록
        stubFor(get(urlEqualTo("/api/v1/comfort/scheduled-result"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(results))));

        // 3. 실제 FeignClient 호출
        ResponseEntity<List<RuleEvaluationResult>> response = comfortAdaptor.getScheduledResult();

        // 4. 검증
        assertEquals(200, response.getStatusCodeValue());
        List<RuleEvaluationResult> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        RuleEvaluationResult result = body.get(0);

        assertEquals(1L, result.getRuleNo());
        assertEquals("Test Rule", result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals("테스트 메시지", result.getMessage());
        assertEquals(LocalDateTime.of(2024, 5, 10, 12, 0), result.getEvaluatedAt());

        assertEquals(1, result.getConditionResults().size());
        ConditionResult cond = result.getConditionResults().get(0);
        assertEquals(100L, cond.getConNo());
        assertEquals("temperature", cond.getConField());
        assertTrue(cond.isMatched());

        assertEquals(1, result.getExecutedActions().size());
        ActionResult act = result.getExecutedActions().get(0);
        assertEquals(200L, act.getActNo());
        assertEquals("ALERT", act.getActType());
        assertTrue(act.isSuccess());
        assertEquals("성공", act.getMessage());
    }

    @NotNull
    private static RuleEvaluationResult getRuleEvaluationResult() {
        RuleEvaluationResult mockResult = new RuleEvaluationResult(1L, "Test Rule", true);
        mockResult.setMessage("테스트 메시지");
        mockResult.setEvaluatedAt(LocalDateTime.of(2024, 5, 10, 12, 0));

        ConditionResult conditionResult = new ConditionResult(
                100L,
                "temperature",
                "GREATER_THAN",
                "25",
                true
        );
        ActionResult actionResult = new ActionResult(200L,
                true,
                "ALERT",
                "성공",
                null,
                LocalDateTime.of(2024, 5, 10, 12, 1)
        );

        mockResult.setConditionResults(List.of(conditionResult));
        mockResult.setExecutedActions(List.of(actionResult));
        return mockResult;
    }
}