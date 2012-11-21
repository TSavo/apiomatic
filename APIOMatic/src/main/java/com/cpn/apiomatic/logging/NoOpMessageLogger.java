package com.cpn.apiomatic.logging;

public class NoOpMessageLogger implements MessageLogger {

	public NoOpMessageLogger() {
	}

	@Override
	public void publish(LoggingMessage msg) {
	}
}
