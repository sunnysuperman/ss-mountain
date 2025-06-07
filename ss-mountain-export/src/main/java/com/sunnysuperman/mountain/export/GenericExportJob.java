package com.sunnysuperman.mountain.export;

import com.sunnysuperman.mountain.lang.utils.Jsons;
import com.sunnysuperman.mountain.repository.annotation.Column;
import com.sunnysuperman.mountain.repository.annotation.Entity;
import com.sunnysuperman.mountain.repository.annotation.Table;

@Entity
@Table(name = "export_job")
public class GenericExportJob extends ExportJob<String> {

	@Column(nullable = false, comment = "模块")
	private String module;

	@Column(nullable = false, comment = "导出者")
	private String operator;

	public static GenericExportJob of(String module, Object params, String operator) {
		GenericExportJob job = ExportJob.of(GenericExportJob.class, paramsAsString(params));
		job.module = module;
		job.operator = operator;
		return job;
	}

	@SuppressWarnings("unchecked")
	public <T> T readParams(Class<T> paramsType) {
		if (paramsType == null) {
			return null;
		}
		if (paramsType == String.class) {
			return (T) paramsType;
		}
		return Jsons.read(getParams(), paramsType);
	}

	private static String paramsAsString(Object params) {
		if (params == null) {
			return null;
		}
		if (params instanceof String) {
			return (String) params;
		}
		return Jsons.write(params);
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

}
