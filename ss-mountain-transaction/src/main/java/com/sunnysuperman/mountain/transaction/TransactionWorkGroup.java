package com.sunnysuperman.mountain.transaction;

public class TransactionWorkGroup implements TransactionAwareWork {
	private TransactionAwareWork[] transactions;

	private TransactionWorkGroup() {
	}

	public static TransactionWorkGroup of(TransactionAwareWork... transactions) {
		TransactionWorkGroup group = new TransactionWorkGroup();
		group.transactions = transactions;
		return group;
	}

	@Override
	public void beforeTransaction() {
		for (TransactionAwareWork transaction : transactions) {
			if (transaction != null) {
				transaction.beforeTransaction();
			}
		}
	}

	@Override
	public void doTransaction() {
		for (TransactionAwareWork transaction : transactions) {
			if (transaction != null) {
				transaction.doTransaction();
			}
		}
	}

	@Override
	public void afterTransaction() {
		for (TransactionAwareWork transaction : transactions) {
			if (transaction != null) {
				transaction.afterTransaction();
			}
		}
	}

}
