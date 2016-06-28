package com.github.tsavo.apiomatic.logging;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;

@Aspect
public class TimedAnnotationMethodLogger {
	@Around("execution(@com.cpn.apiomatic.logging.Timed * * (..))")
	public Object processSystemRequest(final ProceedingJoinPoint pjp) throws Throwable {
		try {
			long start = System.currentTimeMillis();
			Object retVal = pjp.proceed();
			long end = System.currentTimeMillis();
			long differenceMs = end - start;
			MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
			Method targetMethod = methodSignature.getMethod();
			LoggerFactory.getLogger(pjp.getSignature().getDeclaringType()).info(targetMethod.getDeclaringClass().getName() + "." + targetMethod.getName() + " took " + differenceMs + " ms.");
			return retVal;
		} catch (Throwable t) {
			throw t;
		}
	}
}
