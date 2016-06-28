package com.github.tsavo.apiomatic.annotation;

public @interface Optional {
	boolean optional() default true;
	String defaultValue();
}
