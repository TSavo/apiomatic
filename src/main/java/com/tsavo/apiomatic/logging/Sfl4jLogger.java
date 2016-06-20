package com.tsavo.apiomatic.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sfl4jLogger implements MessageLogger {
	Logger logger = LoggerFactory.getLogger(Sfl4jLogger.class);

	public Sfl4jLogger(){
		
	}
	@Override
	public void publish(LoggingMessage msg) {
		logger.info(msg.toString());

	}


}
