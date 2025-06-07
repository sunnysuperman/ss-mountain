package com.sunnysuperman.mountain.evt;

import java.util.List;
import java.util.function.Supplier;

import com.sunnysuperman.mountain.transaction.TransactionAwareWork;

public interface EvtPublisher {

	/**
	 * 发布事件
	 * 
	 * @param event 事件
	 */
	<T extends Evt> void publish(T event);

	/**
	 * 批量发布事件
	 * 
	 * @param events 事件列表
	 */
	<T extends Evt> void publishBatch(List<T> events);

	/**
	 * 在事务上下文中分发事件(生成/保存/发布)
	 * 
	 * @param eventMaker 事件生成函数
	 * @return 事务工作
	 */
	<T extends Evt> TransactionAwareWork publishInTransaction(Supplier<T> eventMaker);

	/**
	 * 在事务上下文中批量分发事件(生成/保存/发布)
	 * 
	 * @param eventsMaker 事件生成函数
	 * @return 事务工作
	 */
	<T extends Evt> TransactionAwareWork publishBatchInTransaction(Supplier<List<T>> eventsMaker);

}
