package com.novel.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PerformanceMonitorAspect {

    @Pointcut("execution(* com.novel.service.impl.*.*(..))")
    public void serviceMethods() {}

    @Pointcut("execution(* com.novel.controller.*.*(..))")
    public void controllerMethods() {}

    @Around("serviceMethods()")
    public Object monitorServicePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            if (duration > 500) {
                log.warn("【性能警告】Service方法执行较慢 - {}.{} 耗时: {}ms", 
                        className, methodName, duration);
            } else if (duration > 100) {
                log.info("【性能监控】Service方法执行时间 - {}.{} 耗时: {}ms", 
                        className, methodName, duration);
            } else {
                log.debug("【性能监控】Service方法执行时间 - {}.{} 耗时: {}ms", 
                        className, methodName, duration);
            }
            
            if (duration > 3000) {
                log.error("【性能严重警告】Service方法执行超时 - {}.{} 耗时: {}ms", 
                        className, methodName, duration);
            }
        }
    }

    @Around("controllerMethods()")
    public Object monitorControllerPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            if (duration > 1000) {
                log.warn("【性能警告】Controller方法执行较慢 - {}.{} 耗时: {}ms", 
                        className, methodName, duration);
            } else {
                log.debug("【性能监控】Controller方法执行时间 - {}.{} 耗时: {}ms", 
                        className, methodName, duration);
            }
        }
    }
}