package com.sunnysuperman.mountain.db;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class PrimarySimpleDBRepository extends DBRepository {
	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	protected JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

}
