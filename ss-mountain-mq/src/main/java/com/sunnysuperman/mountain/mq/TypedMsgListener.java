package com.sunnysuperman.mountain.mq;

import java.util.Properties;

import com.sunnysuperman.mountain.lang.utils.Jsons;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.lang.utils.Types;

public abstract class TypedMsgListener implements MsgListener {

	@Override
	public boolean onMessage(byte[] body, Properties headers) {
		Object msg = castBody(body);
		return onCastMessage(msg, headers);
	}

	protected abstract Class<?> castType();

	protected abstract boolean onCastMessage(Object msg, Properties headers);

	private Object castBody(byte[] body) {
		Class<?> type = castType();
		if (type == null) {
			return null;
		}
		if (type == String.class) {
			return new String(body, Str.UTF8_CHARSET);
		} else if (Types.isByteArray(type)) {
			return body;
		} else {
			return Jsons.read(body, type);
		}
	}
}
