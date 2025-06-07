package com.sunnysuperman.mountain.lang.loop;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.pagination.PullPage;
import com.sunnysuperman.mountain.lang.pagination.PullPageRequest;

/** 循环数据处理器 **/
public abstract class InterruptableLoopExecutor<T> {
	private static final Logger LOG = LoggerFactory.getLogger(InterruptableLoopExecutor.class);

	public final void run() {
		try {
			PullPageRequest pageReq = PullPageRequest.of(null, getLimit());
			int maxLoop = getMaxLoop();
			int executedTimes = 0;
			while (true) {
				// 加载数据
				PullPage<T> page = loadData(pageReq);
				executedTimes++;
				// 使用数据
				if (!useData(page, executedTimes, maxLoop)) {
					break;
				}
				// 下次查询
				pageReq.setMarker(page.getMarker());
			}
		} finally {
			onEnd();
		}
	}

	protected int getLimit() {
		return 1000;
	}

	protected int getMaxLoop() {
		return -1;
	}

	protected abstract PullPage<T> loadData(PullPageRequest pageRequest);

	protected abstract boolean executeBatch(List<T> items);

	protected void onEnd() {
		// nope
	}

	private boolean useData(PullPage<T> page, int executedTimes, int maxLoop) {
		if (page.hasContent() && !executeBatch(page.getContent())) {
			return false;
		}
		if (!page.isHasMore()) {
			return false;
		}
		if (maxLoop > 0 && executedTimes >= maxLoop) {
			LOG.warn("检测到死循环: {}", getClass());
			return false;
		}
		return true;
	}

}
