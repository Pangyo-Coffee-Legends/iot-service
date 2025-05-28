package com.nhnacademy.iot_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.iot_service.redis.sub.RedisSubscriber;
import org.springframework.beans.factory.annotation.Value;
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
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Value("${spring.data.redis.database}")
    private int redisDatabase;

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

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setPassword(RedisPassword.of(redisPassword));  // 꼭 RedisPassword.of() 사용
        config.setDatabase(redisDatabase);
        return new LettuceConnectionFactory(config);
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
