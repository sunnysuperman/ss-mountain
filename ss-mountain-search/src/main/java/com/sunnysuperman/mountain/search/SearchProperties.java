package com.sunnysuperman.mountain.search;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Valid
public class SearchProperties {

	@Valid
	public static class EsProperties {
		@NotEmpty
		private List<String> uris;
		@NotEmpty
		private String username;
		@NotEmpty
		private String password;

		public List<String> getUris() {
			return uris;
		}

		public void setUris(List<String> uris) {
			this.uris = uris;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}

	@NotEmpty
	private EsProperties es;

	/** 是否使用应用名称作为命名空间 **/
	private boolean useAppNameAsNamespace;

	/** 应用名称，默认取spring应用名 **/
	private String appName;

	/** 是否使用环境名称作为命名空间 **/
	private boolean useProfileAsNamespace;

	/** 环境名称，默认取spring环境名(spring.profiles.active) **/
	private String profile;

	public EsProperties getEs() {
		return es;
	}

	public void setEs(EsProperties es) {
		this.es = es;
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
