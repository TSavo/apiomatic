package com.github.tsavo.apiomatic.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Log4jLogger implements MessageLogger {
	Logger logger = LoggerFactory.getLogger(Log4jLogger.class);

	public Log4jLogger(){
		
	}
	@Override
	public void publish(LoggingMessage msg) {
		logger.info(msg.toString());

	}


}
