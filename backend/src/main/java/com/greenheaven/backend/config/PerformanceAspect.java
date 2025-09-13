package com.greenheaven.backend.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PerformanceAspect {

    private final HikariDataSource hikariDataSource;

    @Around("execution(* com.greenheaven.backend.service.*.*(..))") // service 패키지의 모든 메서드에 적용
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 💡 메서드 실행 전 커넥션 풀 상태 확인
        log.info("메서드 실행 전 - 활성화된 커넥션: {}개, 유휴 커넥션: {}개",
                hikariDataSource.getHikariPoolMXBean().getActiveConnections(),
                hikariDataSource.getHikariPoolMXBean().getIdleConnections());

        Object result = joinPoint.proceed(); // 원본 메서드 실행

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        String methodName = joinPoint.getSignature().toShortString();
        log.info("{}의 소요 시간: {}ms", methodName, duration);

        // 💡 메서드 실행 후 커넥션 풀 상태 확인
        log.info("메서드 실행 후 - 활성화된 커넥션: {}개, 유휴 커넥션: {}개",
                hikariDataSource.getHikariPoolMXBean().getActiveConnections(),
                hikariDataSource.getHikariPoolMXBean().getIdleConnections());

        return result;
    }
}
