package com.github.tsavo.apiomatic.documentation;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;

public class DocumentationController {

	public @ResponseBody
	JavaRepresentation getJavaRepresentation(@RequestBody JsonNode aNode) throws Exception {
		return new JsonBeanGenerator().makeClassDescription(aNode);
	}
}
