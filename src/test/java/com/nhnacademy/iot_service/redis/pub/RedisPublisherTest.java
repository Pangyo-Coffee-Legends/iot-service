package com.nhnacademy.iot_service.redis.pub;

import com.nhnacademy.iot_service.dto.action.ActionResult;
import com.nhnacademy.iot_service.dto.condition.ConditionResult;
import com.nhnacademy.iot_service.dto.engine.RuleEvaluationResult;
import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisPublisherTest {

    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @Mock
    ChannelTopic sensorTopic;

    @InjectMocks
    RedisPublisher redisPublisher;

    private final String testTopicName = "sensor-service";
    private final SensorResult testResult = new SensorResult("sensor1", "room1", "ON", List.of(
            new RuleEvaluationResult(
                    101L,
                    "온도 과열 감지",
                    true,
                    List.of(
                            new ConditionResult(1L, "temperature", "GT", "30", true)
                    ),
                    List.of(
                            new ActionResult(1L, true, "EMAIL", "이메일 발송 성공", "mail-123", LocalDateTime.now())
                    ),
                    "온도 30도 초과, 이메일 발송",
                    LocalDateTime.now()
            )
    ));

    @BeforeEach
    void setUp() {
        when(sensorTopic.getTopic()).thenReturn(testTopicName);
    }

    @Test
    @DisplayName("센서 데이터 발행 시 Redis 채널로 메시지 전송")
    void publishSensorData_Success() {
        redisPublisher.publishSensorData(testResult);

        verify(redisTemplate, times(1)).convertAndSend(testTopicName, testResult);
        verifyNoMoreInteractions(redisTemplate);
    }

    @Test
    @DisplayName("정확한 Redis 채널 이름 사용 확인")
    void verifyCorrectTopicUsed() {
        redisPublisher.publishSensorData(testResult);

        verify(sensorTopic, atLeastOnce()).getTopic();
        verify(redisTemplate).convertAndSend(eq(testTopicName), any(SensorResult.class));
    }
}