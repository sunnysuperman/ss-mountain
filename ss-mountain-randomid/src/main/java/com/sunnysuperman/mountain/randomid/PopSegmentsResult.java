package com.sunnysuperman.mountain.randomid;

import java.util.List;

public class PopSegmentsResult {
	private List<String> segments;
	private int remainingNum;

	public PopSegmentsResult(List<String> segments, int remainingNum) {
		super();
		this.segments = segments;
		this.remainingNum = remainingNum;
	}

	public List<String> getSegments() {
		return segments;
	}

	public void setSegments(List<String> segments) {
		this.segments = segments;
	}

	public int getRemainingNum() {
		return remainingNum;
	}

	public void setRemainingNum(int remainingNum) {
		this.remainingNum = remainingNum;
	}

}
