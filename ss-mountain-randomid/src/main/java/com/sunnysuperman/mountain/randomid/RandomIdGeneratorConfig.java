package com.sunnysuperman.mountain.randomid;

import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.randomid.repository.RandomIdRepository;

public class RandomIdGeneratorConfig {
	// 生成器名称(一个服务内需要唯一)
	private String name;
	// id长度
	private int length;
	// 生成最少数量
	private int minNum;
	// id生成存储管理器
	private RandomIdRepository idRepository;
	// 加载时就立刻刷新
	private boolean refreshOnCreated = true;
	// 警告
	private Alarm alarm;

	public void validate() {
		if (Str.isEmpty(name)) {
			throw new IllegalArgumentException("name");
		}
		if (length < 5 || length > 9) {
			throw new IllegalArgumentException("length should be 5~9");
		}
		if (minNum < 0 || minNum > 100000) {
			throw new IllegalArgumentException("minNum should be 1~100000");
		}
		if (idRepository == null) {
			throw new IllegalArgumentException("idRepository");
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getMinNum() {
		return minNum;
	}

	public void setMinNum(int minNum) {
		this.minNum = minNum;
	}

	public RandomIdRepository getIdRepository() {
		return idRepository;
	}

	public void setIdRepository(RandomIdRepository idRepository) {
		this.idRepository = idRepository;
	}

	public boolean isRefreshOnCreated() {
		return refreshOnCreated;
	}

	public void setRefreshOnCreated(boolean refreshOnCreated) {
		this.refreshOnCreated = refreshOnCreated;
	}

	public Alarm getAlarm() {
		return alarm;
	}

	public void setAlarm(Alarm alarm) {
		this.alarm = alarm;
	}

}
