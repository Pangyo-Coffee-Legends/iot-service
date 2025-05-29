package com.nhnacademy.iot_service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Redis 연결 설정 정보를 담는 프로퍼티 클래스입니다.
 * <p>
 * application.yml 또는 application.properties의
 * <code>spring.data.redis</code> 프리픽스 하위 설정값을 자동으로 바인딩합니다.
 * </p>
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {

    /**
     * Redis 서버 호스트명 또는 IP 주소
     */
    private String host;

    /**
     * Redis 서버 포트 번호
     */
    private int port;

    /**
     * Redis 서버 접속 비밀번호
     */
    private String password;

    /**
     * 사용할 Redis 데이터베이스 인덱스 (기본값: 0)
     */
    private int database;
}