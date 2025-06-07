package com.sunnysuperman.mountain.db;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;

public class LogAwareJdbcTemplate extends JdbcTemplate {
	private static final Logger LOG = LoggerFactory.getLogger(LogAwareJdbcTemplate.class);
	private static final String LOG_MSG = "[Jdbc] {}, take {} ms";
	private static final boolean INFO_ENABLED = LOG.isInfoEnabled();

	public LogAwareJdbcTemplate(DataSource dataSource, boolean lazyInit) {
		super(dataSource, lazyInit);
	}

	public LogAwareJdbcTemplate() {
		super();
	}

	public LogAwareJdbcTemplate(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public List<Map<String, Object>> queryForList(String sql, Object... args) throws DataAccessException {
		long t1 = getT1();
		try {
			return super.queryForList(sql, args);
		} finally {
			if (INFO_ENABLED) {
				long take = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t1);
				LOG.info(LOG_MSG, sql, take);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws DataAccessException {
		long t1 = getT1();
		try {
			return super.queryForObject(sql, args, requiredType);
		} finally {
			if (INFO_ENABLED) {
				long take = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t1);
				LOG.info(LOG_MSG, sql, take);
			}
		}
	}

	@Override
	public int update(String sql, Object... args) throws DataAccessException {
		long t1 = getT1();
		try {
			return super.update(sql, args);
		} finally {
			if (INFO_ENABLED) {
				long take = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t1);
				LOG.info(LOG_MSG, sql, take);
			}
		}
	}

	@Override
	public int[] batchUpdate(String sql, List<Object[]> batchArgs) throws DataAccessException {
		long t1 = getT1();
		try {
			return super.batchUpdate(sql, batchArgs);
		} finally {
			if (INFO_ENABLED) {
				long take = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t1);
				LOG.info(LOG_MSG, sql, take);
			}
		}
	}

	@Override
	public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action) throws DataAccessException {
		long t1 = getT1();
		try {
			return super.execute(psc, action);
		} finally {
			if (INFO_ENABLED) {
				long take = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t1);
				if (psc instanceof GeneratKeysPreparedStatementCreator) {
					LOG.info(LOG_MSG, ((GeneratKeysPreparedStatementCreator) psc).getSql(), take);
				}
			}
		}
	}

	private long getT1() {
		return INFO_ENABLED ? System.nanoTime() : 0;
	}

}
