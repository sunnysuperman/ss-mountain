package com.sunnysuperman.mountain.lock;

import java.util.List;
import java.util.stream.Collectors;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.exception.service.FrequencyServiceException;
import com.sunnysuperman.mountain.lang.utils.Str;

public interface LockHelper {

	boolean tryLock(List<String> keys, Runnable work, int timeout, int leaseTime, boolean ignoreLockError);

	boolean tryLock(String key, Runnable work, int timeout, int leaseTime, boolean ignoreLockError);

	default boolean tryLock(Enum<?> key, String subKey, Runnable work, int waitTime, int releaseTime) {
		return tryLock(wrapEnumKey(key, subKey), work, waitTime, releaseTime, false);
	}

	default boolean tryLock(Enum<?> key, List<String> subKeys, Runnable work, int waitTime, int releaseTime) {
		return tryLock(wrapEnumKeys(key, subKeys), work, waitTime, releaseTime, false);
	}

	default boolean tryLock(Enum<?> key, String subKey, Runnable work, int waitTime) {
		return tryLock(key, subKey, work, waitTime, 0);
	}

	default boolean tryLock(Enum<?> key, List<String> subKeys, Runnable work, int waitTime) {
		return tryLock(key, subKeys, work, waitTime, 0);
	}

	default boolean tryLock(Enum<?> key, String subKey, Runnable work) {
		return tryLock(key, subKey, work, 0, 0);
	}

	default boolean tryLock(Enum<?> key, List<String> subKeys, Runnable work) {
		return tryLock(key, subKeys, work, 0, 0);
	}

	default void lock(Enum<?> key, String subKey, Runnable work, int waitTime, int releaseTime) {
		boolean ok = tryLock(key, subKey, work, waitTime, releaseTime);
		if (!ok) {
			throw new FrequencyServiceException();
		}
	}

	default void lock(Enum<?> key, List<String> subKeys, Runnable work, int waitTime, int releaseTime) {
		boolean ok = tryLock(key, subKeys, work, waitTime, releaseTime);
		if (!ok) {
			throw new FrequencyServiceException();
		}
	}

	default void lock(Enum<?> key, String subKey, Runnable work, int waitTime) {
		lock(key, subKey, work, waitTime, 0);
	}

	default void lock(Enum<?> key, List<String> subKeys, Runnable work, int waitTime) {
		lock(key, subKeys, work, waitTime, 0);
	}

	default void lock(Enum<?> key, String subKey, Runnable work) {
		lock(key, subKey, work, 0, 0);
	}

	default void lock(Enum<?> key, List<String> subKeys, Runnable work) {
		lock(key, subKeys, work, 0, 0);
	}

	private String wrapEnumKey(Enum<?> key, String subKey) {
		LockKey lockKey = key.getClass().getAnnotation(LockKey.class);
		String k1 = lockKey == null ? null : lockKey.value();
		if (k1 == null) {
			throw new UnexpectedException("No key defined for lock");
		}
		String k2 = key.name();
		if (subKey == null) {
			subKey = Str.EMPTY;
		}
		StringBuilder buf = new StringBuilder(2 + k1.length() + k2.length() + subKey.length()).append(k1).append(':')
				.append(k2);
		if (!subKey.isEmpty()) {
			buf.append(':').append(subKey);
		}
		return buf.toString();
	}

	private List<String> wrapEnumKeys(Enum<?> key, List<String> subKeys) {
		return subKeys.stream().map(i -> wrapEnumKey(key, i)).collect(Collectors.toList());
	}

}
