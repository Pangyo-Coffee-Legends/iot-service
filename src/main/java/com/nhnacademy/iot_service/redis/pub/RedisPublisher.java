package com.nhnacademy.iot_service.redis.pub;

import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

/**
 * 센서 데이터를 Redis Pub/Sub 채널로 발행(publish)하는 컴포넌트입니다.
 * <p>
 * 센서 결과 데이터를 Redis의 지정된 토픽(채널)으로 전송하여,
 * 구독자(Subscriber)들이 실시간으로 메시지를 받을 수 있도록 합니다.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPublisher {

    /**
     * Redis에 메시지를 발행하기 위한 RedisTemplate입니다.
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 센서 데이터가 발행될 Redis Pub/Sub 채널 토픽입니다.
     */
    private final ChannelTopic sensorTopic;

    /**
     * 센서 결과 데이터를 Redis Pub/Sub 채널로 발행합니다.
     *
     * @param result 발행할 센서 결과 데이터
     */
    public void publishSensorData(SensorResult result) {
        log.debug("Publishing to Redis : {}", result);
        redisTemplate.convertAndSend(sensorTopic.getTopic(), result);
    }
}