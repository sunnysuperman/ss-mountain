package com.sunnysuperman.mountain.lang.utils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunnysuperman.mountain.lang.exception.UnexpectedException;

public final class Jsons {

	private Jsons() {
	}

	private static ObjectMapper getWriteMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		return mapper;
	}

	private static ObjectMapper getReadMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}

	public static String write(Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			return getWriteMapper().writeValueAsString(obj);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static byte[] writeAsBytes(Object obj) {
		try {
			return getWriteMapper().writeValueAsBytes(obj);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static <T> T read(String s, TypeReference<T> ref) {
		ObjectMapper mapper = getReadMapper();
		try {
			return mapper.readValue(s, ref);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static Object read(String s, Class<?> type, Type genericType) {
		ObjectMapper mapper = getReadMapper();
		try {
			if (type.isArray()) {
				return mapper.readerForArrayOf(type.getComponentType()).readValue(s);
			}
			if (Collection.class.isAssignableFrom(type)) {
				Class<?> componentClass = Object.class;
				if (genericType instanceof ParameterizedType) {
					Type componentType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
					if (componentType instanceof Class) {
						componentClass = (Class<?>) componentType;
					}
				}
				if (List.class.isAssignableFrom(type)) {
					return mapper.readerForListOf(componentClass).readValue(s);
				} else if (Set.class.isAssignableFrom(type)) {
					return new HashSet<>(mapper.readerForListOf(componentClass).readValue(s));
				} else {
					throw new UnexpectedException("Failed to parse colection for " + s);
				}
			}
			return mapper.readValue(s, type);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static <T> T read(String s, Class<T> type) {
		ObjectMapper mapper = getReadMapper();
		try {
			return mapper.readerFor(type).readValue(s);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static <T> T read(byte[] bytes, Class<T> type) {
		ObjectMapper mapper = getReadMapper();
		try {
			return mapper.readerFor(type).readValue(bytes);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static <T> List<T> readForList(String s, Class<T> type) {
		ObjectMapper mapper = getReadMapper();
		try {
			return mapper.readerForListOf(type).readValue(s);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static <T> List<T> readForList(byte[] bytes, Class<T> type) {
		ObjectMapper mapper = getReadMapper();
		try {
			return mapper.readerForListOf(type).readValue(bytes);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static Map<String, Object> readForMap(String s) {
		ObjectMapper mapper = getReadMapper();
		try {
			return mapper.readValue(s, new TypeReference<Map<String, Object>>() {
			});
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static <T> T readForParametricType(String s, Class<?> parametrized, JavaType... parameterClasses) {
		ObjectMapper mapper = getReadMapper();
		try {
			return mapper.readValue(s, mapper.getTypeFactory().constructParametricType(parametrized, parameterClasses));
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}
}
