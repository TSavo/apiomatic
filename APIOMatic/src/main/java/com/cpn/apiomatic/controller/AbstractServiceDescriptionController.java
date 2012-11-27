package com.cpn.apiomatic.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cpn.apiomatic.generator.RestController;

public abstract class AbstractServiceDescriptionController {

	@Autowired
	ApplicationContext context;

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.OPTIONS })
	public @ResponseBody
	List<ServiceDescription> getServiceList() {
		List<ServiceDescription> results = new ArrayList<>();
		for (Entry<String, Object> e : context.getBeansWithAnnotation(Controller.class).entrySet()) {
			results.add(new ServiceDescription(e.getKey(), e.getValue().getClass()));
		}
		return results;
	}

	@RequestMapping(value = "/{name}", method = { RequestMethod.GET, RequestMethod.OPTIONS })
	public @ResponseBody
	RestController getRestControllerForClass(@PathVariable("name") String aName) throws ClassNotFoundException {
		return new RestController(context.getBean(aName).getClass());
	}

}
