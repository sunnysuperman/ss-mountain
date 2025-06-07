package com.sunnysuperman.mountain.transaction;

public abstract class EmbeddedTransactionWork implements TransactionAwareWork {
	TransactionAwareWork embedded;

	@Override
	public final void doTransaction() {
		embedded = doTransactionAndEmbed();
		if (embedded != null) {
			embedded.doTransaction();
		}
	}

	@Override
	public final void afterTransaction() {
		doAfterTransaction();
		if (embedded != null) {
			embedded.afterTransaction();
		}
	}

	protected abstract TransactionAwareWork doTransactionAndEmbed();

	protected void doAfterTransaction() {
		// nope
	}

}