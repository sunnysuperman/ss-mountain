package com.sunnysuperman.mountain.transaction;

/**
 * 事务内容
 */
public interface TransactionAwareWork {

	/**
	 * 执行事务之前(如:生成对象)
	 */
	default void beforeTransaction() {
		// nope
	}

	/**
	 * 执行事务(如:保存对象)
	 */
	void doTransaction();

	/**
	 * 执行事务之后(如:清缓存,发布事件等)
	 */
	default void afterTransaction() {
		// nope
	}
}