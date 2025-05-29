package com.nhnacademy.iot_service.config;

import com.nhnacademy.iot_service.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC의 웹 설정을 담당하는 설정 클래스입니다.
 * <p>
 * 인증 인터셉터(AuthInterceptor)를 모든 요청 경로에 적용하며,
 * 정적 리소스(css, js, images)는 제외합니다.
 * </p>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 인증 처리를 위한 인터셉터입니다.
     */
    private final AuthInterceptor authInterceptor;

    /**
     * AuthInterceptor를 주입받아 WebMvcConfig를 생성합니다.
     *
     * @param authInterceptor 인증 인터셉터
     */
    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    /**
     * 인증 인터셉터를 모든 요청 경로에 등록합니다.
     * <p>
     * 단, 정적 리소스 경로(/css/**, /js/**, /images/**)는 인터셉터 적용 대상에서 제외합니다.
     * </p>
     *
     * @param registry 인터셉터 레지스트리
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/css/**",
                        "/js/**",
                        "/images/**"
                );
    }
}