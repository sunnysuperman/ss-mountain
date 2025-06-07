package com.sunnysuperman.mountain.randomid.repository;

import java.util.Date;

import com.sunnysuperman.mountain.randomid.PopSegmentsResult;

public interface RandomIdRepository {

	PopSegmentsResult popSegments(int bits, int num, Date date);

}
