package com.sunnysuperman.mountain.evt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sunnysuperman.mountain.base.BaseProperties;
import com.sunnysuperman.mountain.db.DBAutoConfiguration;
import com.sunnysuperman.mountain.lock.LockHelper;
import com.sunnysuperman.mountain.mq.MQAutoConfiguration;
import com.sunnysuperman.mountain.mq.MQProducerManager;
import com.sunnysuperman.mountain.mq.MessageListenerManager;
import com.sunnysuperman.mountain.task.TaskAutoConfiguration;
import com.sunnysuperman.mountain.task.TaskHelper;
import com.sunnysuperman.mountain.transaction.TransactionHelper;

@Configuration(proxyBeanMethods = false)
@Import({ TaskAutoConfiguration.class, DBAutoConfiguration.class, MQAutoConfiguration.class })
public class EvtAutoConfiguration {

	@Bean
	@ConfigurationProperties("ss-mountain.evt")
	public EvtProperties evtProperties() {
		return new EvtProperties();
	}

	@Bean
	public EvtClassManager evtClassManager(EvtProperties evtProperties, BaseProperties baseProperties) {
		return new EvtClassManager(evtProperties, baseProperties);
	}

	@Bean
	public EvtRepositoryManager evtRepositoryManager(EvtClassManager evtClassMananger, JdbcTemplate jdbcTemplate,
			@SuppressWarnings("rawtypes") @Autowired(required = false) List<EvtRepository> repositoryList) {
		return new EvtRepositoryManager(evtClassMananger, jdbcTemplate, repositoryList);
	}

	@Bean
	public EvtListenerManager evtListenerManager() {
		return new EvtListenerManager();
	}

	@Bean
	public EvtConsumer evtConsumer(EvtListenerManager evtListenerManager, EvtRepositoryManager evtRepositoryManager,
			LockHelper lockHelper) {
		return new EvtConsumer(evtListenerManager, evtRepositoryManager, lockHelper);
	}

	@Bean
	public EvtJobScheduler evtJobScheduler(EvtClassManager evtClassMananger, EvtRepositoryManager evtRepositoryManager,
			EvtConsumer evtConsumer) {
		return new EvtJobScheduler(evtClassMananger, evtRepositoryManager, evtConsumer);
	}

	@Bean
	public EvtMQConsumer evtMQConsumer(EvtProperties evtProperties, EvtConsumer evtConsumer,
			EvtClassManager evtClassMananger, MessageListenerManager messageListenerManager) {
		return new EvtMQConsumer(evtConsumer, evtClassMananger, messageListenerManager);
	}

	@Bean
	public EvtPublisher evtPublisher(EvtProperties evtProperties, EvtRepositoryManager evtRepositoryManager,
			EvtConsumer evtConsumer, TaskHelper taskHelper, TransactionHelper transactionHelper,
			MQProducerManager mqProducerManager) {
		return new EvtPublisherImpl(evtProperties, evtRepositoryManager, evtConsumer, taskHelper, transactionHelper,
				mqProducerManager);
	}

}
