package com.sunnysuperman.mountain.export;

import java.util.Date;

import com.sunnysuperman.mountain.lang.utils.Num;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.lang.utils.Types;
import com.sunnysuperman.mountain.repository.annotation.Column;
import com.sunnysuperman.mountain.repository.annotation.Id;
import com.sunnysuperman.mountain.repository.annotation.IdStrategy;

public class ExportJob<T> {

	@Id(strategy = IdStrategy.INCREMENT)
	@Column
	private Long id;

	@Column(updatable = false, nullable = false, comment = "创建时间")
	private Date createdDate;

	@Column(nullable = false, comment = "修改时间")
	private Date updatedDate;

	@Column(updatable = false, nullable = false, length = 64, comment = "密钥")
	private String secret;

	@Column(nullable = false, comment = "状态")
	private ExportState state;

	@Column(nullable = false, comment = "进度")
	private Byte progress;

	@Column(length = 1024, comment = "输出URL")
	private String outputUrl;

	@Column(length = 3000, comment = "错误信息")
	private String errorMsg;

	@Column(updatable = false, comment = "导出参数")
	private T params;

	public static <P, T extends ExportJob<P>> T of(Class<T> type, P params) {
		T instance = Types.newInstance(type);
		instance.init(params);
		return instance;
	}

	public ExportJob() {
		super();
	}

	public ExportJob(T params) {
		super();
		init();
		this.params = params;
	}

	public void init() {
		createdDate = new Date();
		updatedDate = createdDate;
		secret = Str.randomAlphanumeric(64);
		state = ExportState.INIT;
		progress = Num.BYTE_0;
	}

	public void init(T params) {
		init();
		this.params = params;
	}

	public void start() {
		state = ExportState.ING;
		progress = 1;
		updatedDate = new Date();
	}

	public void updateAsSuccess(String outputUrl) {
		this.outputUrl = outputUrl;
		state = ExportState.SUCCESS;
		progress = 100;
		updatedDate = new Date();
	}

	public void updateAsError(String errorMsg) {
		this.errorMsg = errorMsg;
		state = ExportState.ERROR;
		updatedDate = new Date();
	}

	public boolean updateProgress(int progress) {
		if (progress < this.progress) {
			return false;
		}
		final byte max = 99;
		this.progress = progress > max ? max : (byte) progress;
		this.state = ExportState.ING;
		updatedDate = new Date();
		return true;
	}

	public void forView() {
		secret = null;
		createdDate = null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public ExportState getState() {
		return state;
	}

	public void setState(ExportState state) {
		this.state = state;
	}

	public String getOutputUrl() {
		return outputUrl;
	}

	public void setOutputUrl(String outputUrl) {
		this.outputUrl = outputUrl;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Byte getProgress() {
		return progress;
	}

	public void setProgress(Byte progress) {
		this.progress = progress;
	}

	public T getParams() {
		return params;
	}

	public void setParams(T params) {
		this.params = params;
	}
}
