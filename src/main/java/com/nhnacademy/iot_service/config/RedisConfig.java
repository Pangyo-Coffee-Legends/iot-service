package com.nhnacademy.iot_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 관련 설정을 정의하는 구성 클래스입니다.
 * Redis 연결 팩토리, 템플릿, Pub/Sub 채널 토픽 등을 설정합니다.
 */
@Configuration
public class RedisConfig {

    /**
     * Redis Pub/Sub 채널 토픽을 생성합니다.
     *
     * @return "sensor-service" 채널명을 가진 {@link ChannelTopic} 빈
     * @see ChannelTopic
     */
    @Bean
    public ChannelTopic sensorTopic() {
        return new ChannelTopic("sensor-service"); // 원하는 채널명으로 지정
    }

    /**
     * Redis 연결 팩토리를 생성합니다.
     * 기본 로컬호스트(127.0.0.1)와 포트(6379)를 사용하여 연결을 설정합니다.
     *
     * @return Lettuce 기반 Redis 연결 팩토리
     * @see LettuceConnectionFactory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    /**
     * Redis 데이터 접근을 위한 템플릿을 구성합니다.<br>
     * Key/Value 직렬화 방식을 다음과 같이 설정합니다:
     * <ul>
     *   <li>Key: 문자열 직렬화({@link StringRedisSerializer})</li>
     *   <li>Value: JSON 직렬화({@link GenericJackson2JsonRedisSerializer})</li>
     *   <li>Hash Key/Value: 동일한 직렬화 방식 적용</li>
     * </ul>
     *
     * @return 구성된 Redis 템플릿 객체
     * @see GenericJackson2JsonRedisSerializer
     * @see JavaTimeModule
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        // Java LocalDateTime Add
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.activateDefaultTyping( // 타입 정보 포함
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setValueSerializer(serializer);

        // Key: String 직렬화
        template.setKeySerializer(new StringRedisSerializer());

        // Value: JSON 직렬화
        template.setValueSerializer(serializer);

        // Hash Key/Value 직렬화
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }
}
