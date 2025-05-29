package com.nhnacademy.iot_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.iot_service.properties.RedisProperties;
import com.nhnacademy.iot_service.redis.sub.RedisSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 관련 설정을 정의하는 구성 클래스입니다.
 * Redis 연결 팩토리, 템플릿, Pub/Sub 채널 토픽 등을 설정합니다.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

    private final RedisProperties redisProperties;

    public RedisConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

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
     * Redis 연결 팩토리 빈을 생성합니다.
     * <p>
     * RedisStandaloneConfiguration을 사용하여 Redis 서버의 호스트, 포트, 비밀번호, 데이터베이스를 설정하고,
     * LettuceConnectionFactory를 반환합니다.
     * </p>
     *
     * @return LettuceConnectionFactory Redis 연결을 관리하는 팩토리 객체
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());
        config.setPassword(RedisPassword.of(redisProperties.getPassword()));  // 꼭 RedisPassword.of() 사용
        config.setDatabase(redisProperties.getDatabase());
        return new LettuceConnectionFactory(config);
    }

    /**
     * Redis 메시지 리스너 컨테이너 빈을 생성합니다.
     * <p>
     * 주어진 RedisConnectionFactory, RedisSubscriber, ChannelTopic을 사용하여
     * RedisMessageListenerContainer를 구성하고, 지정된 토픽에 대한 메시지 리스너를 등록합니다.
     * </p>
     *
     * @param connectionFactory Redis 연결 팩토리
     * @param redisSubscriber   Redis 메시지 리스너(구독자)
     * @param sensorTopic       구독할 Redis 채널 토픽
     * @return RedisMessageListenerContainer Redis Pub/Sub 메시지 리스너 컨테이너
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisSubscriber redisSubscriber,
            ChannelTopic sensorTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(redisSubscriber, sensorTopic); // 이 라인이 핵심!
        return container;
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
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

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
