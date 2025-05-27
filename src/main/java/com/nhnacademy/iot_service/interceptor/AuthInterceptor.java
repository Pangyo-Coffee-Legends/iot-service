package com.nhnacademy.iot_service.interceptor;

import com.nhnacademy.iot_service.auth.MemberThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * 요청 전처리: 인증된 사용자의 멤버 번호를 ThreadLocal에 저장합니다.
     *
     * @param request  현재 HTTP 요청 객체
     * @param response 현재 HTTP 응답 객체
     * @param handler  실행할 핸들러(컨트롤러 메서드)
     * @return 인증 실패 시 false 반환하여 요청 중단, 성공 시 true
     * @throws Exception 내부 예외 발생 시 상위 계층으로 전파
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String email = request.getHeader("X-USER");

        if (Objects.isNull(email) || email.isBlank()) {
            log.error("register sensor unauthorized");
            response.sendError(HttpStatus.FORBIDDEN.value(), "로그인 해주세요");
            return false;
        }

        MemberThreadLocal.setMemberEmail(email);

        return true;
    }

    /**
     * 요청 후처리: ThreadLocal에서 멤버 번호를 제거합니다.
     *
     * @param request  현재 HTTP 요청 객체
     * @param response 현재 HTTP 응답 객체
     * @param handler  실행된 핸들러
     * @param ex       요청 처리 중 발생한 예외 (있을 경우)
     * @throws Exception 내부 예외 발생 시 상위 계층으로 전파
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MemberThreadLocal.removedMemberEmail();
        log.debug("memberThreadLocal remove success!");
    }
}
