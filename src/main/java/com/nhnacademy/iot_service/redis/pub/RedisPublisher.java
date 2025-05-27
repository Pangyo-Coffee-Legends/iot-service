package com.nhnacademy.iot_service.redis.pub;

import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic sensorTopic;

    public void publishSensorData(SensorResult result) {
        log.debug("Publishing to Redis : {}", result);
        redisTemplate.convertAndSend(sensorTopic.getTopic(), result);
    }
}
