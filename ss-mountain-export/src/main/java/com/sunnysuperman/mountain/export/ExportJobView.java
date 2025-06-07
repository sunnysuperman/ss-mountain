package com.sunnysuperman.mountain.export;

public class ExportJobView {
	private Long id;

	private String secret;

	private ExportState state;

	private String outputUrl;

	private String errorMsg;

	private Byte progress;

	public static ExportJobView of(ExportJob<?> job) {
		ExportJobView view = new ExportJobView();
		view.id = job.getId();
		view.state = job.getState();
		view.outputUrl = job.getOutputUrl();
		view.errorMsg = job.getErrorMsg();
		view.progress = job.getProgress();
		return view;
	}

	public static ExportJobView forMissing(Long id) {
		ExportJobView view = new ExportJobView();
		view.id = id;
		view.state = ExportState.ING;
		view.progress = 0;
		return view;
	}

	public static ExportJobView forCreated(ExportJob<?> job) {
		ExportJobView view = of(job);
		view.secret = job.getSecret();
		return view;
	}

	public static ExportJobView forCreated(ExportJob<?> job, String secret) {
		if (job == null) {
			return null;
		}
		if (secret == null || !job.getSecret().equals(secret)) {
			return null;
		}
		return forCreated(job);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
}
