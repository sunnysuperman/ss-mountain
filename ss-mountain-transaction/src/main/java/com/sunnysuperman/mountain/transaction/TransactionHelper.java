package com.sunnysuperman.mountain.transaction;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sunnysuperman.mountain.lang.exception.Exceptions;

/** 事务助手 **/
public class TransactionHelper {
	private TransactionTemplate transactionTemplate;

	public TransactionHelper(TransactionTemplate transactionTemplate) {
		super();
		this.transactionTemplate = transactionTemplate;
	}

	private static class MyTransactionCallback implements TransactionCallback<Void> {
		private TransactionAwareWork[] works;
		private Throwable exception;

		public MyTransactionCallback(TransactionAwareWork[] works) {
			super();
			this.works = works;
		}

		@SuppressWarnings("squid:S1181")
		@Override
		public Void doInTransaction(TransactionStatus status) {
			try {
				for (TransactionAwareWork work : works) {
					if (work != null) {
						work.doTransaction();
					}
				}
				return null;
			} catch (Throwable t) {
				exception = t;
				status.setRollbackOnly();
				return null;
			}
		}

		public Throwable getException() {
			return exception;
		}

	}

	/**
	 * 执行事务
	 * 
	 * @param works 事务项(1个或多个)
	 */
	public void doTransaction(TransactionAwareWork... works) {
		for (TransactionAwareWork work : works) {
			if (work != null) {
				try {
					work.beforeTransaction();
				} catch (Exception e) {
					throw Exceptions.wrapRuntimeException(e);
				}
			}
		}
		MyTransactionCallback callback = new MyTransactionCallback(works);
		transactionTemplate.execute(callback);
		Throwable exception = callback.getException();
		if (exception != null) {
			throw Exceptions.wrapRuntimeException(exception);
		}
		for (TransactionAwareWork work : works) {
			if (work != null) {
				try {
					work.afterTransaction();
				} catch (Exception e) {
					throw Exceptions.wrapRuntimeException(e);
				}
			}
		}
	}

	public void doWithoutTransaction(TransactionAwareWork... works) {
		for (TransactionAwareWork work : works) {
			if (work != null) {
				try {
					work.beforeTransaction();
				} catch (Exception e) {
					throw Exceptions.wrapRuntimeException(e);
				}
			}
		}
		onTransaction(works);
		for (TransactionAwareWork work : works) {
			if (work != null) {
				try {
					work.afterTransaction();
				} catch (Exception e) {
					throw Exceptions.wrapRuntimeException(e);
				}
			}
		}
	}

	private void onTransaction(TransactionAwareWork[] works) {
		for (TransactionAwareWork work : works) {
			if (work != null) {
				try {
					work.doTransaction();
				} catch (Exception e) {
					throw Exceptions.wrapRuntimeException(e);
				}
			}
		}
	}

}
