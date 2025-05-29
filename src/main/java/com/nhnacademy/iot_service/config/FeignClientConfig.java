package com.nhnacademy.iot_service.config;

import com.nhnacademy.traceloggermodule.config.FeignTraceInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 클라이언트의 공통 설정을 정의하는 설정 클래스입니다.
 * <p>
 * Feign 요청에 대한 인터셉터 등 공통 Bean을 등록할 때 사용합니다.
 * </p>
 */
@Configuration
public class FeignClientConfig {

    /**
     * Feign 요청에 트레이스 정보를 추가하는 인터셉터 빈을 등록합니다.
     * <p>
     * 이 인터셉터는 모든 Feign 요청에 대해 공통적으로 동작하며,
     * 예를 들어 트레이스 ID, 인증 토큰, 커스텀 헤더 등을 추가할 수 있습니다.
     * </p>
     *
     * @return FeignTraceInterceptor Feign 요청 인터셉터 빈
     */
    @Bean
    public RequestInterceptor feignTraceInterceptor() {
        return new FeignTraceInterceptor();
    }
}