package com.tsavo.apiomatic.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.tsavo.apiomatic.annotation.Null;
import com.tsavo.apiomatic.annotation.Optional;

public class TypeDefinitionFactory {
	public static TypeDefinition getTypeDefinition(Class<?> clazz, Annotation[] someAnnotations, Type aType) {
		TypeDefinition type;
		if (clazz.equals(String.class)) {
			return new StringType();
		}
		if (clazz.equals(Integer.class) || clazz.equals(BigInteger.class)) {
			return new IntegerType();
		}
		if (clazz.equals(Long.class) || clazz.equals(Double.class)) {
			return new NumberType();
		}
		if (clazz.equals(List.class) || clazz.equals(Set.class)) {
			return new ArrayType(getTypeDefinition(((ParameterizedType) aType).getActualTypeArguments()[0].getClass(), clazz.getAnnotations(), clazz.getTypeParameters()[0]));
		}
		type = new ObjectType(clazz);
		outer: for (Field f : clazz.getDeclaredFields()) {
			String name = f.getName();
			TypeDefinition innerType = getTypeDefinition(f.getClass(), f.getAnnotations(), f.getType());
			for (Annotation a : f.getAnnotations()) {
				if (a instanceof JsonProperty) {
					name = ((JsonProperty) a).value();
				}
				if (a instanceof JsonIgnore) {
					continue outer;
				}
			}
			((ObjectType) type).addProperty(name, innerType);
		}
		for (Annotation annotation : someAnnotations) {
			if (annotation instanceof Null) {
				type = new NullType();
			}
			if (annotation instanceof Optional) {
				type.setOptional(((Optional) annotation).optional());
			}
		}
		return type;
	}
}
