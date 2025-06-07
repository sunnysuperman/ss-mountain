package com.sunnysuperman.mountain.job.xxl;

public class XxlJobProperties {

	public static class JobExecutorConfig {
		private String appname;
		private String address;
		private String ip;
		private int port;
		private String logPath = "logs/job";
		private int logRetentionDays = 3;

		public String getAppname() {
			return appname;
		}

		public void setAppname(String appname) {
			this.appname = appname;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getLogPath() {
			return logPath;
		}

		public void setLogPath(String logPath) {
			this.logPath = logPath;
		}

		public int getLogRetentionDays() {
			return logRetentionDays;
		}

		public void setLogRetentionDays(int logRetentionDays) {
			this.logRetentionDays = logRetentionDays;
		}

	}

	private String adminAddresses;
	private String accessToken;
	private JobExecutorConfig executor;

	public String getAdminAddresses() {
		return adminAddresses;
	}

	public void setAdminAddresses(String adminAddresses) {
		this.adminAddresses = adminAddresses;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public JobExecutorConfig getExecutor() {
		return executor;
	}

	public void setExecutor(JobExecutorConfig executor) {
		this.executor = executor;
	}

}