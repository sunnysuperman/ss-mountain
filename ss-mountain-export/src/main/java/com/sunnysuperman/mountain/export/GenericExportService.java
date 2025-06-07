package com.sunnysuperman.mountain.export;

public interface GenericExportService {

	/** 导出 **/
	ExportJobView export(String module, Object params, String operator);

	/** 查询导出结果 **/
	ExportJobView getExportResult(String module, Long id, String secret);

}
