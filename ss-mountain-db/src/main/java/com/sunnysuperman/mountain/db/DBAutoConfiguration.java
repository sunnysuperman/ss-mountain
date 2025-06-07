package com.sunnysuperman.mountain.db;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.sunnysuperman.mountain.base.BaseProperties;
import com.sunnysuperman.mountain.lang.utils.Obj;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.transaction.TransactionHelper;
import com.zaxxer.hikari.HikariDataSource;

@Configuration(proxyBeanMethods = false)
public class DBAutoConfiguration {
	private static final Logger LOG = LoggerFactory.getLogger(DBAutoConfiguration.class);

	@Resource
	private BaseProperties baseProperties;

	public static void initEntities(DBProperties dbProperties, BaseProperties baseProperties) {
		// 初始化实体(扫描实体包)
		String[] packages = Obj.or(dbProperties.getScanPackages(), baseProperties.getScanPackages());
		if (LOG.isInfoEnabled()) {
			LOG.info(">>>>>>[db] entity-manager initializes with packages: {}", Str.join(packages));
		}
		EntityManager.scan(packages);
	}

	@Primary
	@Bean("dbProperties")
	@ConfigurationProperties("ss-mountain.db")
	public DBProperties dbProperties() {
		return new DBProperties();
	}

	@Primary
	@Bean("dataSource")
	@ConfigurationProperties("ss-mountain.db.datasource")
	public DataSource dataSource(DBProperties dbProperties, BaseProperties baseProperties) {
		// 初始化实体
		initEntities(dbProperties, baseProperties);
		return DataSourceBuilder.create().driverClassName("com.mysql.cj.jdbc.Driver").type(HikariDataSource.class)
				.build();
	}

	@Primary
	@Bean("jdbcTemplate")
	public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource dataSource, DBProperties dbProperties) {
		return dbProperties.isLog() ? new LogAwareJdbcTemplate(dataSource, dbProperties.isLazyInit())
				: new JdbcTemplate(dataSource, dbProperties.isLazyInit());
	}

	@Primary
	@Bean("transactionManager")
	public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Primary
	@Bean("transactionTemplate")
	public TransactionTemplate transactionTemplate(
			@Qualifier("transactionManager") PlatformTransactionManager transactionManager) {
		TransactionTemplate template = new TransactionTemplate(transactionManager);
		template.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
		template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		return template;
	}

	@Primary
	@Bean("transactionHelper")
	public TransactionHelper transactionsHelper(
			@Qualifier("transactionTemplate") TransactionTemplate transactionTemplate) {
		return new TransactionHelper(transactionTemplate);
	}
}
