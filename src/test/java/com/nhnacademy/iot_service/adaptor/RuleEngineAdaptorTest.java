package com.nhnacademy.iot_service.adaptor;

import com.nhnacademy.iot_service.dto.action.ActionResult;
import com.nhnacademy.iot_service.dto.condition.ConditionResult;
import com.nhnacademy.iot_service.dto.engine.RuleEvaluationResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "rule-engine-service.url=http://localhost:${mock.web.server.port}"
})
class RuleEngineAdaptorTest {

    static MockWebServer mockWebServer;

    @Autowired
    RuleEngineAdaptor ruleEngineAdaptor;

    @BeforeAll
    static void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("mock.web.server.port", () -> mockWebServer.getPort());
    }

    @AfterAll
    static void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("룰 엔진 실행 성공")
    void executeTriggeredRules_success() throws Exception {
        String mockResponseBody = """
                [
                    {
                        "ruleNo": 1,
                        "ruleName": "High Temperature Alert",
                        "success": true,
                        "conditionResults": [
                            {
                                "conNo": 101,
                                "conField": "temperature",
                                "conType": "GT",
                                "conValue": "30",
                                "matched": true
                            }
                        ],
                        "executedActions": [
                            {
                                "actNo": 201,
                                "success": true,
                                "actType": "EMAIL",
                                "message": "Email sent successfully",
                                "output": "email-123",
                                "executedAt": "2025-05-08T10:00:00"
                            }
                        ],
                        "message": "Rule executed successfully",
                        "evaluatedAt": "2025-05-08T10:00:00"
                    }
                ]
                """;
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(mockResponseBody));

        Map<String, Object> facts = Map.of("temperature", 35, "location", "Seoul");
        ResponseEntity<List<RuleEvaluationResult>> response = ruleEngineAdaptor.executeTriggeredRules(
                "TEMPERATURE_EVENT",
                "{\"threshold\":30}",
                facts
        );

        // 요청 검증
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/api/v1/rule-engine/trigger",
                recordedRequest.getRequestUrl().encodedPath());
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"));

        // 응답 검증
        List<RuleEvaluationResult> results = response.getBody();
        assertNotNull(results);
        assertEquals(1, results.size());

        RuleEvaluationResult result = results.get(0);
        assertEquals(1L, result.getRuleNo());
        assertEquals("High Temperature Alert", result.getRuleName());
        assertTrue(result.isSuccess());

        ConditionResult condition = result.getConditionResults().get(0);
        assertEquals(101L, condition.getConNo());
        assertEquals("temperature", condition.getConField());
        assertTrue(condition.isMatched());

        ActionResult action = result.getExecutedActions().get(0);
        assertEquals(201L, action.getActNo());
        assertEquals("EMAIL", action.getActType());
        assertTrue(action.isSuccess());
    }

    @Test
    @DisplayName("특정 룰을 수동으로 실행 성공")
    void executeRule_success() throws Exception {
        String mockResponseBody = """
                {
                    "ruleNo": 2,
                    "ruleName": "Manual Override Rule",
                    "success": false,
                    "conditionResults": [
                        {
                            "conNo": 102,
                            "conField": "pressure",
                            "conType": "LT",
                            "conValue": "100",
                            "matched": false
                        }
                    ],
                    "executedActions": [],
                    "message": "Conditions not met",
                    "evaluatedAt": "2025-05-08T11:00:00"
                }
                """;
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(mockResponseBody));

        Map<String, Object> facts = Map.of("pressure", 150);
        ResponseEntity<RuleEvaluationResult> response = ruleEngineAdaptor.executeRule(
                2L,
                facts
        );

        // 요청 검증
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/api/v1/rule-engine/manual/2", recordedRequest.getPath());
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"));

        // 응답 검증
        RuleEvaluationResult result = response.getBody();
        assertNotNull(result);
        assertEquals(2L, result.getRuleNo());
        assertEquals("Manual Override Rule", result.getRuleName());
        assertFalse(result.isSuccess());
        assertEquals(0, result.getExecutedActions().size());
    }
}