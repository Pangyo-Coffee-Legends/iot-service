package com.nhnacademy.iot_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
class RedisConnectionTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testRedisConnection() {
        // 데이터 저장
        String key = "testKey";
        String value = "testValue";
        redisTemplate.opsForValue().set(key, value);

        // 데이터 조회
        String result = (String) redisTemplate.opsForValue().get(key);
        assertEquals(value, result);
    }
}
