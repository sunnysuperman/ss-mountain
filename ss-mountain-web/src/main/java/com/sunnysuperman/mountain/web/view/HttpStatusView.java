package com.sunnysuperman.mountain.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.view.AbstractView;

public class HttpStatusView extends AbstractView {
	private HttpStatus status;
	private boolean withStatusMessage;

	public HttpStatusView(HttpStatus status) {
		super();
		this.status = status;
		this.withStatusMessage = true;
	}

	public HttpStatusView(HttpStatus status, boolean withStatusMessage) {
		super();
		this.status = status;
		this.withStatusMessage = withStatusMessage;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setStatus(status.value());
		if (withStatusMessage) {
			response.getWriter().println(status.getReasonPhrase());
			response.getWriter().flush();
		}
	}

}
