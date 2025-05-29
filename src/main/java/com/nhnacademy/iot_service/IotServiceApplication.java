package com.nhnacademy.iot_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * IoT 서비스의 Spring Boot 애플리케이션 메인 클래스입니다.
 * <p>
 * - Feign 클라이언트 활성화<br>
 * - 서비스 디스커버리(Eureka 등) 클라이언트 활성화<br>
 * - AOP(AspectJ) 자동 프록시 활성화<br>
 * - Spring Boot 애플리케이션 구동
 * </p>
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class IotServiceApplication {

    /**
     * IoT 서비스 애플리케이션의 진입점(Main) 메서드입니다.
     *
     * @param args 커맨드라인 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(IotServiceApplication.class, args);
    }
}