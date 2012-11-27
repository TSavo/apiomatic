package com.cpn.apiomatic.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.sf.cglib.proxy.Enhancer;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
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
	List<ServiceDescription> getServiceList() throws ClassNotFoundException {
		List<ServiceDescription> results = new ArrayList<>();
		for (Entry<String, Object> e : context.getBeansWithAnnotation(Controller.class).entrySet()) {
			Class<?> c = e.getValue().getClass();
			if (ClassUtils.isCglibProxyClass(c)) {
				c = ClassUtils.getUserClass(c);
			}

			results.add(new ServiceDescription(e.getKey(), c));
		}
		return results;
	}

	@RequestMapping(value = "/{name}", method = { RequestMethod.GET, RequestMethod.OPTIONS })
	public @ResponseBody
	RestController getRestControllerForClass(@PathVariable("name") String aName) throws ClassNotFoundException {
		Class<?> c = context.getBean(aName).getClass();
		if (ClassUtils.isCglibProxyClass(c)) {
			c = ClassUtils.getUserClass(c);
		}
		return new RestController(c);
	}

}
