package com.sunnysuperman.mountain.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

import com.sunnysuperman.mountain.lang.utils.Jsons;

public class JsonView extends AbstractView {
	private Object content;

	public JsonView(Object content) {
		super();
		this.content = content;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("application/json; charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.getWriter().write(Jsons.write(content));
	}
}
