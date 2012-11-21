package com.cpn.apiomatic.annotation;

public @interface Optional {
	boolean optional() default true;
	String defaultValue();
}
