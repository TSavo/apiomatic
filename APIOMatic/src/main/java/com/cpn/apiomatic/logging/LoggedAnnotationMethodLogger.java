package com.cpn.apiomatic.logging;

import java.io.Serializable;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.cpn.apiomatic.logging.LoggingMessage.State;

@Aspect
public class LoggedAnnotationMethodLogger {

	private static LoggingMessage makeMessage(final State aState, final JoinPoint aJoinPoint, final Object aResult) {
		final LoggingMessage message = new LoggingMessage();
		message.state = aState;
		message.className = aJoinPoint.getSignature().getDeclaringTypeName().toString();
		message.methodName = aJoinPoint.getSignature().getName();
		for (final Object o : aJoinPoint.getArgs()) {
			if ((o instanceof Serializable) || (o == null)) {
				message.arguments.add(o);
			} else if (o != null) {
				message.arguments.add(o.toString());
			}
		}
		message.threadId = Thread.currentThread().toString();
		if (aResult instanceof Serializable) {
			message.result = aResult;
		} else if (aResult != null) {
			message.result = aResult.toString();
		}
		return message;
	}

	private final MessageLogger logger;

	public LoggedAnnotationMethodLogger(final MessageLogger logger) {
		this.logger = logger;
	}

	@AfterReturning(pointcut = "execution(@com.cpn.logging.Logged * * (..))", returning = "result")
	public void logAPICallsAfter(final JoinPoint joinPoint, final Object result) {

		logger.publish(LoggedAnnotationMethodLogger.makeMessage(State.EXIT, joinPoint, result));
	}

	@AfterThrowing(pointcut = "execution(@com.cpn.logging.Logged * * (..))", throwing = "error")
	public void logAPICallsAfterThrow(final JoinPoint joinPoint, final Throwable error) {
		logger.publish(LoggedAnnotationMethodLogger.makeMessage(State.THROWN, joinPoint, error.getMessage()));
	}

	@Before(value = "execution(@com.cpn.logging.Logged * * (..))")
	public void logAPICallsBefore(final JoinPoint joinPoint) {
		logger.publish(LoggedAnnotationMethodLogger.makeMessage(State.ENTER, joinPoint, null));
	}

}