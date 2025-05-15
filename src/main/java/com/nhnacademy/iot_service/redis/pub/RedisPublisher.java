package com.nhnacademy.iot_service.redis.pub;

import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic sensorTopic;

    public void publishSensorData(SensorResult result) {
        redisTemplate.convertAndSend(sensorTopic.getTopic(), result);
    }
}
