/*
package com.innocito.testpilot.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AspectConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(AspectConfiguration.class);

    @Before("execution(* com.innocito.testpilot..*.*(..))")
    public void before(JoinPoint joinPoint) {
        logger.info("Start of Method: " + joinPoint.getSignature());
    }

    @AfterReturning("execution(* com.innocito.testpilot..*.*(..))")
    public void normalClosing(JoinPoint joinPoint) {
        logger.info("End of the method: " + joinPoint.getSignature());
    }

    @AfterThrowing(pointcut = "execution(* com.innocito.testpilot..*.*(..))", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Throwable exception) throws Throwable {
        logger.error("Error occurred at " + joinPoint.getSignature() + "\n" + exception.getMessage());
    }

    @Around("execution(* com.innocito.testpilot.controller..*(..))))")
    public Object executionTime(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object object = point.proceed();
        long endTime = System.currentTimeMillis();
        logger.debug("Perf :: {\"className\":\"" + point.getSignature().getDeclaringTypeName() + "\",\"methodName\":\" "
                + point.getSignature().getName() + " \",\"timeInMillis\":\" " + (endTime - startTime) + "\"}");
        return object;
    }
}*/
