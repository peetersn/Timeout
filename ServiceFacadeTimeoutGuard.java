package com.myapp.service.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple aspect that profiles the execution time for methods. 
 * 
 * @author npeeters
 */
@Aspect
public class ServiceFacadeTimeoutGuard {

	static final Logger logger = LoggerFactory.getLogger(ServiceFacadeTimeoutGuard.class);

	private static final int generationTimeout = 30000; //30s
	
	/**
	 * Defines the pointcut expression
	 */
	@Pointcut("execution(!void com.myapp..*Service.*(..))")
	public void serviceMethods() {
		// the name of this method "serviceMethods" is actually just a name for the pointcut
		// this method is actually never invoked.
	}

	@SuppressWarnings("deprecation")
	@Around("serviceMethods()")
	public Object profile(ProceedingJoinPoint pjp) throws Throwable {

		TimeoutGuard timer = new TimeoutGuard(generationTimeout, pjp.toShortString());
		timer.start();

		
		long start = System.currentTimeMillis();
		logger.debug("Going to call the method.");
		Object output = pjp.proceed();
		logger.debug("Method execution completed.");
		long elapsedTime = System.currentTimeMillis() - start;
		logger.info("Method execution time: {} ms", elapsedTime);
		
		/** Resets the timer back to zero */
		timer.reset();
		
		/** Shutdown timer */
		timer.stop();

		return output;
	}
}