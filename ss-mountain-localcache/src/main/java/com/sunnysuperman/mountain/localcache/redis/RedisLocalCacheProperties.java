package com.sunnysuperman.mountain.localcache.redis;

public class RedisLocalCacheProperties {
	private String host;
	private int port = 6379;
	private String password;
	private int db = -1;
	private int minIdle;
	private int maxIdle;
	private int maxTotal;
	private int idleTime = 60 * 10;
	private int maxWait = 5;

	/** 是否使用应用名称作为命名空间 **/
	private boolean useAppNameAsNamespace;
	/** 应用名称，默认取spring应用名 **/
	private String appName;
	/** 是否使用环境名称作为命名空间 **/
	private boolean useProfileAsNamespace;
	/** 环境名称，默认取spring环境名(spring.profiles.active) **/
	private String profile;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDb() {
		return db;
	}

	public void setDb(int db) {
		this.db = db;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getIdleTime() {
		return idleTime;
	}

	public void setIdleTime(int idleTime) {
		this.idleTime = idleTime;
	}

	public int getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}

	public boolean isUseAppNameAsNamespace() {
		return useAppNameAsNamespace;
	}

	public void setUseAppNameAsNamespace(boolean useAppNameAsNamespace) {
		this.useAppNameAsNamespace = useAppNameAsNamespace;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public boolean isUseProfileAsNamespace() {
		return useProfileAsNamespace;
	}

	public void setUseProfileAsNamespace(boolean useProfileAsNamespace) {
		this.useProfileAsNamespace = useProfileAsNamespace;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

}
