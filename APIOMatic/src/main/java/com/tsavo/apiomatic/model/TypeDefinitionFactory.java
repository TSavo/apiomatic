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
	public static TypeDefinition getTypeDefinition(final Class<?> clazz, final Annotation[] someAnnotations, final Type aType) {
		TypeDefinition type;
		if (clazz.equals(String.class)) {
			return new StringType();
		}
		if (clazz.equals(Integer.class) || clazz.equals(Long.class) || clazz.equals(Short.class) || clazz.equals(BigInteger.class) || clazz.getName().equals("int") || clazz.getName().equals("short") || clazz.getName().equals("long")) {
			return new IntegerType();
		}
		if (clazz.equals(Float.class) || clazz.equals(Double.class) || clazz.getName().equals("float") || clazz.getName().equals("double")) {
			return new NumberType();
		}
		if (clazz.equals(List.class) || clazz.equals(Set.class)) {
			return new ArrayType(TypeDefinitionFactory.getTypeDefinition(((ParameterizedType) aType).getActualTypeArguments()[0].getClass(), clazz.getAnnotations(), clazz.getTypeParameters()[0]));
		}
		type = new ObjectType(clazz);
		outer: for (final Field f : clazz.getDeclaredFields()) {
			if (f.toString().contains("static")) {
				continue;
			}
			String name = f.getName();
			for (final Annotation a : f.getAnnotations()) {
				if (a instanceof JsonProperty) {
					name = ((JsonProperty) a).value();
				}
				if (a instanceof JsonIgnore) {
					continue outer;
				}
			}
			final TypeDefinition innerType = TypeDefinitionFactory.getTypeDefinition(f.getType(), f.getAnnotations(), f.getType());
			((ObjectType) type).addProperty(name, innerType);
		}
		for (final Annotation annotation : someAnnotations) {
			if (annotation instanceof Null) {
				type = new NullType();
			}
		}
		for (final Annotation annotation : someAnnotations) {
			if (annotation instanceof Optional) {
				type.setOptional(((Optional) annotation).optional());
			}
		}
		return type;
	}
}
