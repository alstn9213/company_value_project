package com.back.global.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* com.back..*Controller.*(..))")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String handlerName = joinPoint.getSignature().toShortString();

        // [REQUEST] 로그
        log.info("--> [REQUEST] {} {} | Handler: {}", method, uri, handlerName);

        try {
            Object result = joinPoint.proceed(); // 실제 메서드 실행

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // [RESPONSE] 로그 (실행 시간 포함)
            log.info("<-- [RESPONSE] {} {} | Time: {}ms", method, uri, executionTime);
            return result;

        } catch (Exception e) {
            // [EXCEPTION] 로그
            log.error("<X- [EXCEPTION] {} {} | Message: {}", method, uri, e.getMessage());
            throw e; // 예외를 다시 던져서 GlobalExceptionHandler가 처리하게 함
        }
    }
}
