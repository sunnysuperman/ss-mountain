package com.sunnysuperman.mountain.evt;

import com.sunnysuperman.mountain.lang.pagination.PullPage;
import com.sunnysuperman.mountain.lang.pagination.PullPageRequest;
import com.sunnysuperman.mountain.repository.CRUDRepository;

public interface EvtRepository<T extends Evt> extends CRUDRepository<T, Long> {

	PullPage<T> findForPullPage(PullPageRequest pageRequest);

}
