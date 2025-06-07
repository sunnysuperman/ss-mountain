package com.sunnysuperman.mountain.db;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.sunnysuperman.mountain.db.mapper.DBMapper;
import com.sunnysuperman.mountain.lang.pagination.Page;
import com.sunnysuperman.mountain.lang.pagination.PageRequest;
import com.sunnysuperman.mountain.lang.pagination.PullPage;
import com.sunnysuperman.mountain.lang.pagination.PullPageRequest;

public abstract class PrimaryDBRepository<T, I> extends DBCRUDRepository<T, I> {
	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	protected JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Override
	protected DefaultFieldConverter getDefaultFieldConverter() {
		return BuiltInDefautFieldConverter.getInstance();
	}

	protected T find(SqlBuilder builder) {
		return find(builder.sql(), builder.params());
	}

	protected <M> M find(SqlBuilder builder, DBMapper<M> mapper) {
		return find(builder.sql(), builder.params(), mapper);
	}

	protected <M> List<M> findForList(SqlBuilder builder, int offset, int limit, DBMapper<M> mapper) {
		return findForList(builder.sql(), builder.params(), offset, limit, mapper);
	}

	protected List<T> findForList(SqlBuilder builder, int offset, int limit) {
		return findForList(builder.sql(), builder.params(), offset, limit);
	}

	protected List<T> findForList(SqlBuilder builder, int limit) {
		return findForList(builder.sql(), builder.params(), 0, limit);
	}

	protected List<T> findForList(SqlBuilder builder) {
		return findForList(builder.sql(), builder.params(), 0, 0);
	}

	protected <M> Page<M> findForPage(SqlBuilder builder, PageRequest page, DBMapper<M> mapper) {
		return findForPage(builder.sql(), builder.params(), page, mapper);
	}

	protected Page<T> findForPage(SqlBuilder builder, PageRequest page) {
		return findForPage(builder.sql(), builder.params(), page);
	}

	protected <M> PullPage<M> findForPullPage(SqlBuilder builder, PullPageRequest page, DBMapper<M> mapper) {
		return findForPullPage(builder.sql(), builder.params(), page, mapper);
	}

	protected PullPage<T> findForPullPage(SqlBuilder builder, PullPageRequest page) {
		return findForPullPage(builder.sql(), builder.params(), page);
	}

	protected <M> PullPage<M> findForPullPageByColumn(SqlBuilder builder, String column, PullPageRequest page,
			DBMapper<M> mapper) {
		return findForPullPageByColumn(builder.sql(), builder.params(), column, page.getLimit(), mapper);
	}

	protected PullPage<T> findForPullPageByColumn(SqlBuilder builder, String column, PullPageRequest page) {
		return findForPullPageByColumn(builder.sql(), builder.params(), column, page);
	}

}
