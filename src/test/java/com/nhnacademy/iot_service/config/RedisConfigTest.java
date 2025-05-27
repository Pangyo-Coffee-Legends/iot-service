package com.nhnacademy.iot_service.config;

import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class RedisConfigTest {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    ChannelTopic sensorTopic;

    private final String testKey = "test:sensor-result";

    @AfterEach
    void tearDown() {
        redisTemplate.delete(testKey);
    }

    @Test
    @DisplayName("RedisTemplate 에 SensorResult 를 저장하고 다시 읽으면 값이 동일 해야 한다.")
    void redisTemplate_SensorResult_serialize_deserialize() {
        SensorResult original = new SensorResult(
                "sensor",
                "room1",
                "ON",
                List.of()
        );

        redisTemplate.opsForValue().set(testKey, original);
        Object value = redisTemplate.opsForValue().get(testKey);

        assertNotNull(value);
        assertInstanceOf(SensorResult.class, value);

        SensorResult restored = (SensorResult) value;
        assertEquals(original.getSensorName(), restored.getSensorName());
        assertEquals(original.getLocation(), restored.getLocation());
        assertEquals(original.getStatus(), restored.getStatus());
        assertEquals(original.getRuleResults(), restored.getRuleResults());
    }

    @Test
    @DisplayName("Redis Pub/Sub로 SensorResult를 발행하면 구독자가 메시지를 받을 수 있다")
    void redisPubSub_publish_and_subscribe() throws Exception {
        SensorResult result = new SensorResult("sensor2", "room2", "OFF", List.of());

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<SensorResult> received = new AtomicReference<>();

        MessageListener listener = getListener(received, latch);

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        container.addMessageListener(listener, sensorTopic);
        container.afterPropertiesSet();
        container.start();

        redisTemplate.convertAndSend(sensorTopic.getTopic(), result);

        boolean messageReceived = latch.await(2, TimeUnit.SECONDS);
        assertTrue(messageReceived, "2초 내 메시지 수신 실패");
        assertNotNull(received.get(), "수신된 메시지 없음");
        assertEquals(result.getSensorName(), received.get().getSensorName());

        container.stop();
    }

    @NotNull
    private MessageListener getListener(AtomicReference<SensorResult> received, CountDownLatch latch) {
        GenericJackson2JsonRedisSerializer serializer =
                (GenericJackson2JsonRedisSerializer) redisTemplate.getValueSerializer();

        return (message, pattern) -> {
            try {
                // RedisSerializer로 역직렬화
                SensorResult r = (SensorResult) serializer.deserialize(message.getBody());
                received.set(r);
                latch.countDown();
            } catch (Exception e) {
                log.error("역직렬화 실패", e);
            }
        };
    }

}