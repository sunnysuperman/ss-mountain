package com.sunnysuperman.mountain.export;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.sunnysuperman.mountain.db.DBAutoConfiguration;
import com.sunnysuperman.mountain.task.TaskAutoConfiguration;
import com.sunnysuperman.mountain.task.TaskHelper;

@Configuration(proxyBeanMethods = false)
@Import({ TaskAutoConfiguration.class, DBAutoConfiguration.class })
public class ExportAutoConfiguration {

	@Bean
	public GenericExportJobRepository genericExportJobRepository() {
		return new GenericExportJobRepositoryImpl();
	}

	@Bean
	public GenericExportService genericExportService(TaskHelper taskHelper,
			GenericExportJobRepository genericExportJobRepository,
			@Autowired(required = false) List<GenericExportJobExecutor> genericExportJobExecutors) {
		return new GenericExportServiceImpl(taskHelper, genericExportJobRepository, genericExportJobExecutors);
	}

}
