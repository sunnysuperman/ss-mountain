package com.sunnysuperman.mountain.lang.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sunnysuperman.mountain.lang.exception.UnexpectedException;

public final class Jsons {

	// 编码&解码器
	private static final ObjectMapper READ_MAPPER;
	private static final ObjectMapper WRITE_MAPPER;
	// 缓存ObjectReader
	private static final Map<Class<?>, ObjectReader> OBJECT_READERS = new ConcurrentHashMap<>();
	private static final Map<Class<?>, ObjectReader> LIST_READERS = new ConcurrentHashMap<>();
	private static final Map<Class<?>, ObjectReader> ARRAY_READERS = new ConcurrentHashMap<>();
	private static final Map<Field, ObjectReader> FIELD_READERS = new ConcurrentHashMap<>();
	// 缓存针对 Map<String, Object> 的 ObjectReader
	private static final ObjectReader MAP_READER;
	// 缓存针对 List<Map<String, Object>> 的 ObjectReader
	private static final ObjectReader MAP_LIST_READER;
	// 空字节数据
	private static final byte[] NULL_BYTE_ARRAY = null;

	static {
		READ_MAPPER = getReadMapper();
		WRITE_MAPPER = getWriteMapper();
		MAP_READER = READ_MAPPER.readerFor(new TypeReference<Map<String, Object>>() {
		});
		MAP_LIST_READER = READ_MAPPER.readerFor(new TypeReference<List<Map<String, Object>>>() {
		});
	}

	private Jsons() {
	}

	public static String write(Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			return WRITE_MAPPER.writeValueAsString(obj);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static byte[] writeAsBytes(Object obj) {
		if (obj == null) {
			return NULL_BYTE_ARRAY;
		}
		try {
			return WRITE_MAPPER.writeValueAsBytes(obj);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static <T> T read(byte[] bytes, Class<T> type) {
		try {
			return getObjectReader(type).readValue(bytes);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static <T> T read(String s, Class<T> type) {
		try {
			return getObjectReader(type).readValue(s);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static <T> T read(String s, TypeReference<T> ref) {
		try {
			return READ_MAPPER.readValue(s, ref);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static <T> T read(String s, JavaType javaType) {
		try {
			return READ_MAPPER.readValue(s, javaType);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static Object read(String s, Field field) {
		try {
			ObjectReader reader = FIELD_READERS.computeIfAbsent(field, f -> {
				JavaType javaType = READ_MAPPER.getTypeFactory().constructType(f.getGenericType());
				return READ_MAPPER.readerFor(javaType);
			});
			return reader.readValue(s);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static <T> List<T> readForList(byte[] bytes, Class<T> type) {
		try {
			return getListReader(type).readValue(bytes);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static <T> List<T> readForList(String s, Class<T> type) {
		try {
			return getListReader(type).readValue(s);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static <T> T[] readForArray(String s, Class<T> type) {
		try {
			ObjectReader reader = ARRAY_READERS.computeIfAbsent(type, k -> READ_MAPPER.readerForArrayOf(type));
			return reader.readValue(s);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static <T> Set<T> readForSet(String s, Class<T> type) {
		List<T> list = readForList(s, type);
		return new HashSet<>(list);
	}

	public static Map<String, Object> readForMap(String s) {
		try {
			return MAP_READER.readValue(s);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static List<Map<String, Object>> readForMapList(String json) {
		try {
			return MAP_LIST_READER.readValue(json);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static <T> T readForParametricType(String s, Class<?> parametrized, JavaType... parameterClasses) {
		try {
			return READ_MAPPER.readValue(s,
					READ_MAPPER.getTypeFactory().constructParametricType(parametrized, parameterClasses));
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public static ObjectMapper getWriteMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		return mapper;
	}

	public static ObjectMapper getReadMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}

	public static ObjectReader getReader(TypeReference<?> type) {
		return READ_MAPPER.readerFor(type);
	}

	public static ObjectReader getReader(JavaType javaType) {
		return READ_MAPPER.readerFor(javaType);
	}

	public static ObjectReader getReader(Class<?> parametrized, Class<?>... parameterClasses) {
		return READ_MAPPER
				.readerFor(READ_MAPPER.getTypeFactory().constructParametricType(parametrized, parameterClasses));
	}

	public static ObjectReader getReader(Class<?> parametrized, JavaType... parameterClasses) {
		return READ_MAPPER
				.readerFor(READ_MAPPER.getTypeFactory().constructParametricType(parametrized, parameterClasses));
	}

	private static ObjectReader getObjectReader(Class<?> type) {
		return OBJECT_READERS.computeIfAbsent(type, k -> READ_MAPPER.readerFor(type));
	}

	private static ObjectReader getListReader(Class<?> type) {
		return LIST_READERS.computeIfAbsent(type, k -> READ_MAPPER.readerForListOf(type));
	}

}
