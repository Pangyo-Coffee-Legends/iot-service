package com.nhnacademy.iot_service.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DisplayName("/admin 경로는 인증 없이 접근하면 FORBIDDEN(403)")
    void testAdminAccessDeniedWithoutRole() {
        ResponseEntity<String> response = restTemplate.getForEntity("/admin", String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }


}