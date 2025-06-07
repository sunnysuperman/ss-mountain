package com.sunnysuperman.mountain.export;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.exception.service.ArgumentServiceException;
import com.sunnysuperman.mountain.task.TaskHelper;

public class GenericExportServiceImpl implements GenericExportService {
	private static final Logger LOG = LoggerFactory.getLogger(GenericExportServiceImpl.class);

	private TaskHelper taskHelper;
	private GenericExportJobRepository genericExportJobRepository;
	private List<GenericExportJobExecutor> genericExportJobExecutors;

	private Map<String, GenericExportJobExecutor> genericExportJobExecutorMap;

	public GenericExportServiceImpl(TaskHelper taskHelper, GenericExportJobRepository genericExportJobRepository,
			List<GenericExportJobExecutor> genericExportJobExecutors) {
		super();
		this.taskHelper = taskHelper;
		this.genericExportJobRepository = genericExportJobRepository;
		this.genericExportJobExecutors = genericExportJobExecutors;
	}

	@PostConstruct
	public void init() {
		if (genericExportJobExecutors == null || genericExportJobExecutors.isEmpty()) {
			return;
		}
		genericExportJobExecutorMap = genericExportJobExecutors.stream()
				.collect(Collectors.toMap(i -> i.module(), i -> i));
	}

	@Override
	public ExportJobView export(String module, Object params, String operator) {
		GenericExportJobExecutor executor = genericExportJobExecutorMap.get(module);
		if (executor == null) {
			throw new ArgumentServiceException("module");
		}
		// 保存任务
		GenericExportJob job = GenericExportJob.of(module, params, operator);
		genericExportJobRepository.insert(job);
		// 异步执行任务
		execute(() -> {
			try {
				executor.execute(job);
			} catch (Exception e) {
				LOG.error(null, e);
			}
		});
		// 返回任务ID和密钥
		return ExportJobView.forCreated(job);
	}

	@Override
	public ExportJobView getExportResult(String module, Long id, String secret) {
		GenericExportJob job = genericExportJobRepository.findById(id);
		if (job == null || !job.getSecret().equals(secret)) {
			return ExportJobView.forMissing(id);
		}
		return ExportJobView.of(job);
	}

	protected void execute(Runnable runnable) {
		taskHelper.addTask(runnable);
	}

}
