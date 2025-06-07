package com.sunnysuperman.mountain.lang.test.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Callable;

import org.junit.jupiter.api.Test;

import com.sunnysuperman.mountain.lang.utils.Retryer;

class RetryerTest {

	private class MyCallable implements Callable<Void> {
		int successAt;
		int i;

		public MyCallable(int successAt) {
			super();
			this.successAt = successAt;
		}

		@Override
		public Void call() throws Exception {
			i++;
			if (i != successAt) {
				throw new RuntimeException("不成功");
			}
			System.out.println("成功了，在第" + i + "次");
			return null;
		}

	}

	@Test
	void testCall() {
		Retryer retryer = new Retryer().setMaxAttempts(4);

		MyCallable callable0 = new MyCallable(0);
		try {
			retryer.call(callable0);
			assertTrue(false);
		} catch (Exception ex) {
			// ignore
		}

		MyCallable callable1 = new MyCallable(1);
		retryer.call(callable1);
		assertEquals(1, callable1.i);

		MyCallable callable2 = new MyCallable(2);
		retryer.call(callable2);
		assertEquals(2, callable2.i);

		MyCallable callable3 = new MyCallable(3);
		retryer.call(callable3);
		assertEquals(3, callable3.i);

		MyCallable callable4 = new MyCallable(4);
		retryer.call(callable4);
		assertEquals(4, callable4.i);

		try {
			retryer.call(new MyCallable(5));
			assertTrue(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		MyCallable callable5 = new MyCallable(5);
		retryer.setMaxAttempts(5).call(callable5);
		assertEquals(5, callable5.i);

		try {
			retryer.setMaxAttempts(5).setLogger(null).call(new MyCallable(6));
			assertTrue(false);
		} catch (Exception ex) {
			// ignore
		}
	}
}
