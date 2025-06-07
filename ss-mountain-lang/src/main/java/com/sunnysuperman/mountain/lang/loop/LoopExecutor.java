package com.sunnysuperman.mountain.lang.loop;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.pagination.PullPage;
import com.sunnysuperman.mountain.lang.pagination.PullPageRequest;

/** 循环数据处理器 **/
public abstract class LoopExecutor<T> {

	private static final Logger LOG = LoggerFactory.getLogger(LoopExecutor.class);

	private int executedTimes = 0;

	public final void run() {
		try {
			PullPageRequest pageRequest = PullPageRequest.of(null, getLimit());
			while (loadAndExecuteBatch(pageRequest)) {
				// nope
			}
		} finally {
			onEnd();
		}
	}

	private boolean loadAndExecuteBatch(PullPageRequest pageRequest) {
		// 加载数据
		PullPage<T> page = loadData(pageRequest);
		// 使用数据
		if (page.hasContent()) {
			executeBatch(page.getContent());
		}
		executedTimes++;
		// 下个循环准备
		if (!page.isHasMore()) {
			return false;
		}
		pageRequest.setMarker(page.getMarker());
		// 检测死循环
		int maxLoop = getMaxLoop();
		if (maxLoop > 0 && executedTimes >= maxLoop) {
			LOG.warn("检测到死循环: {}", getClass());
			return false;
		}
		return true;
	}

	protected int getLimit() {
		return 1000;
	}

	protected int getMaxLoop() {
		return -1;
	}

	protected abstract PullPage<T> loadData(PullPageRequest pageRequest);

	protected abstract void executeBatch(List<T> items);

	protected void onEnd() {
		// nope
	}
}
