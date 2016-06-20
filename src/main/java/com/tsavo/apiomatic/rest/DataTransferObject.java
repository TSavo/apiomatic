package com.tsavo.apiomatic.rest;

import java.io.Serializable;

public interface DataTransferObject<T> extends Serializable {

	public T getId();
}
