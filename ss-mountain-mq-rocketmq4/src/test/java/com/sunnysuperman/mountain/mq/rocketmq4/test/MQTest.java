package com.sunnysuperman.mountain.mq.rocketmq4.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import com.sunnysuperman.mountain.base.DefaultAutoConfigurations;
import com.sunnysuperman.mountain.lang.utils.ProcessUtil;
import com.sunnysuperman.mountain.mq.MQProduceOptions;
import com.sunnysuperman.mountain.mq.MQProducer;
import com.sunnysuperman.mountain.mq.rocketmq4.test.MQTest.MQApp;

@SpringBootTest(classes = MQApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.config.location=classpath:/application.yml,classpath:/application-mq.yml" })
class MQTest {
	@Resource
	private MQProducer producer;
	@Resource
	private MyService service;

	@SpringBootApplication
	@Import({ DefaultAutoConfigurations.class })
	@ComponentScan({ "com.sunnysuperman.mountain.mq.rocketmq4.test" })
	public static class MQApp {

		public static void main(String[] args) {
			SpringApplication.run(MQApp.class, args);
		}
	}

	@Test
	void testPublishAndConsume() {
		List<String> messages = new ArrayList<>();
		long suffix = System.currentTimeMillis();
		for (int i = 0; i < 4; i++) {
			messages.add("消息" + (i + 1) + ":" + suffix);
		}
		String badMessage = "消息不可达:" + suffix;

		producer.produce(new M2Message(badMessage), new MQProduceOptions().setTag(M2Message.TAG));
		producer.produce("m2", new M2Message(messages.get(2)), new MQProduceOptions().setTag(M2Message.TAG));
		producer.produce("m2", new M2Message2(messages.get(3)), new MQProduceOptions().setTag(M2Message2.TAG));
		producer.produce(new DefaultMessage(messages.get(0)), new MQProduceOptions().setTag(DefaultMessage.TAG));
		producer.produce("mm", new MMMessage(messages.get(1)), new MQProduceOptions().setTag(MMMessage.TAG));

		boolean ok = false;
		int times = 0;
		while (++times <= 10) {
			Set<String> msgs = service.getConsumedMessages();
			if (msgs.containsAll(messages) && !msgs.contains(badMessage)) {
				ok = true;
				break;
			}
			ProcessUtil.sleep(1000);
		}
		assertTrue(ok);
	}

}
