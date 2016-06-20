package com.tsavo.apiomatic.logging;

public class NoOpMessageLogger implements MessageLogger {

	public NoOpMessageLogger() {
	}

	@Override
	public void publish(LoggingMessage msg) {
	}
}
