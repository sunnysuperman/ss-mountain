package com.sunnysuperman.mountain.db.mapper;

import java.util.Map;

import com.sunnysuperman.mountain.lang.utils.Num;

@SuppressWarnings("squid:S6548")
public class ByteDBMapper implements DBMapper<Byte> {
	private ByteDBMapper() {
	}

	@Override
	public Byte map(Map<String, Object> doc) {
		if (doc.isEmpty()) {
			return null;
		}
		return Num.parseByte(doc.values().iterator().next());
	}

	private static final ByteDBMapper INSTANCE = new ByteDBMapper();

	public static final ByteDBMapper getInstance() {
		return INSTANCE;
	}

}
