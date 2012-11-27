package com.cpn.apiomatic.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

public abstract class AbstractServiceDescriptionController {

	@Autowired
	ApplicationContext context;

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.OPTIONS })
	public @ResponseBody
	List<ServiceDescription> getServiceList() {
		List<ServiceDescription> results = new ArrayList<>();
		for (Object o : context.getBeansWithAnnotation(Controller.class).values()) {
			results.add(new ServiceDescription(o.getClass()));
		}
		return results;
	}

}
