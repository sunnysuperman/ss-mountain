package com.sunnysuperman.mountain.evt;

import java.util.Date;

import com.sunnysuperman.mountain.db.PrimaryDBRepository;
import com.sunnysuperman.mountain.lang.pagination.MarkerUtils;
import com.sunnysuperman.mountain.lang.pagination.PullPage;
import com.sunnysuperman.mountain.lang.pagination.PullPageRequest;

public abstract class AbstractEvtRepository<T extends Evt> extends PrimaryDBRepository<T, Long>
		implements EvtRepository<T> {
	private String sqlForPullPage;

	@Override
	public PullPage<T> findForPullPage(PullPageRequest pageRequest) {
		String min = pageRequest.getMarker() != null ? pageRequest.getMarker() : MarkerUtils.getMinDateMarker();
		String max = MarkerUtils.getDateMarker(new Date());
		Object[] params = new Object[] { min, max };
		return findForPullPageByColumn(getSqlForPullPage(), params, "_scheduled_time", pageRequest);
	}

	private String getSqlForPullPage() {
		if (sqlForPullPage == null) {
			sqlForPullPage = "select * from " + getTable()
					+ " where _scheduled_time>? and _scheduled_time<? order by _scheduled_time asc";
		}
		return sqlForPullPage;
	}

}
