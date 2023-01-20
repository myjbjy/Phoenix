package com.myjbjy.api;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * @author myj
 */
@Component
@Aspect
@Slf4j
public class ServiceLogAspect {

    @Around("execution(* com.myjbjy.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch();

        long begin = System.currentTimeMillis();

        stopWatch.start("task1");
        Object proceed = joinPoint.proceed();
        String point = joinPoint.getTarget().getClass().getName()
                + "."
                + joinPoint.getSignature().getName();
        stopWatch.stop();

        stopWatch.start("task2");
        Thread.sleep(350);
        stopWatch.stop();

        stopWatch.start("task3");
        Thread.sleep(550);
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());
        log.info(stopWatch.shortSummary());
        System.out.println(stopWatch.getTotalTimeMillis());
        System.out.println(stopWatch.getTaskCount());

        long end = System.currentTimeMillis();
        long takeTime = end - begin;

        if (takeTime > 3000) {
            log.error("执行方法：{} 执行时间太长了，耗费了{}毫秒", point, takeTime);
        } else if (takeTime > 2000) {
            log.warn("执行方法：{} 执行时间稍微有点长，耗费了{}毫秒", point, takeTime);
        } else {
            log.info("执行方法：{} 执行时间，耗费了{}毫秒", point, takeTime);
        }

        return proceed;
    }
}
