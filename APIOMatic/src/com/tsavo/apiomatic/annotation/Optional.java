package com.tsavo.apiomatic.annotation;

public @interface Optional {
	boolean optional() default true;
}
