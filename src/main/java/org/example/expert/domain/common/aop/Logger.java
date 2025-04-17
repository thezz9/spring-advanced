package org.example.expert.domain.common.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class Logger {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("@annotation(org.example.expert.domain.common.aop.AdminLoggingTarget)")
    private void adminApi() {}

    @Around("adminApi()")
    public Object doAdminLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // 요청 정보
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String requestUrl = request.getRequestURL().toString();
        Long userId = (Long) request.getAttribute("userId");

        // 요청 본문
        String requestBody = getRequestBody(joinPoint);

        // API 요청 시각
        LocalDateTime requestTime = LocalDateTime.now();

        // 요청 로그 출력
        logRequest(userId, requestTime, requestUrl, requestBody);

        // 메서드 실행 후 응답 본문 받기
        Object result = joinPoint.proceed();

        // 응답 본문
        String responseBody = objectMapper.writeValueAsString(result);

        // 응답 로그 출력
        logResponse(userId, requestTime, responseBody);

        return result;
    }

    private String getRequestBody(ProceedingJoinPoint joinPoint) {
        try {
            for (Object arg : joinPoint.getArgs()) {
                if (arg != null && arg.getClass().getPackageName().contains("dto")) {
                    return objectMapper.writeValueAsString(arg);
                }
            }
        } catch (IOException e) {
            log.error("Request body parsing failed", e);
        }
        return "No RequestBody";
    }

    private void logRequest(Long userId, LocalDateTime requestTime, String requestUrl, String requestBody) {
        log.info("Request: UserID: {}, Time: {}, URL: {}, RequestBody: {}", userId, requestTime, requestUrl, requestBody);
    }

    private void logResponse(Long userId, LocalDateTime requestTime, String responseBody) {
        log.info("Response: UserID: {}, Time: {}, ResponseBody: {}", userId, requestTime, responseBody);
    }

}
