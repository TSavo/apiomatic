package com.cpn.apiomatic.rest;

import java.io.Serializable;

public interface DataTransferObject<T> extends Serializable {

	public T getId();
}
