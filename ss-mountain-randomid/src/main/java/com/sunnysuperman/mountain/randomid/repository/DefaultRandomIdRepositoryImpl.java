package com.sunnysuperman.mountain.randomid.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sunnysuperman.mountain.db.PrimarySimpleDBRepository;
import com.sunnysuperman.mountain.db.mapper.MapDBMapper;
import com.sunnysuperman.mountain.lang.utils.Num;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.randomid.PopSegmentsResult;
import com.sunnysuperman.mountain.randomid.RandomIdUtils;
import com.sunnysuperman.mountain.repository.RepositoryException;

public abstract class DefaultRandomIdRepositoryImpl extends PrimarySimpleDBRepository implements RandomIdRepository {
	private String findSql;
	private String updateSql;

	@Override
	public PopSegmentsResult popSegments(int bits, int num, Date date) {
		int totalNum = (int) Math.pow(10, bits);
		Integer day = RandomIdUtils.date2day(date);
		Integer bitsWithSep = bits + 1;
		Map<String, Object> row = find(getFindSql(), new Object[] { bitsWithSep, bitsWithSep * num, day },
				MapDBMapper.getInstance());
		if (row != null) {
			Long version = Num.parseLong(row.get("version"));
			boolean ok = execute(getUpdateSql(), new Object[] { num, day, version }) > 0;
			if (!ok) {
				throw new RepositoryException("Failed to update id sequence");
			}
			int remainingNum = totalNum - num - Num.parseInteger(row.get("idx"));
			return new PopSegmentsResult(Str.split(Str.parse(row.get("v")), ","), remainingNum);
		}

		String segments = createSegments(bits);
		row = new HashMap<>();
		row.put("day", day);
		row.put("version", Num.LONG_1);
		row.put("val", segments);
		row.put("idx", num);
		insertDoc(getTable(), row);

		List<String> segmentList = Str.split(segments, ",", num);
		return new PopSegmentsResult(segmentList, totalNum - num);
	}

	private String createSegments(int bits) {
		int totalNum = (int) Math.pow(10, bits);
		int[] digitArray = RandomIdUtils.randomArrayOfMax(totalNum);
		StringBuilder segmentsBuf = new StringBuilder(digitArray.length * (bits + 1));
		int i = 0;
		char sep = ',';
		for (int digit : digitArray) {
			if (i > 0) {
				segmentsBuf.append(sep);
			}
			segmentsBuf.append(Num.pad(digit, bits));
			i++;
		}
		return segmentsBuf.toString();
	}

	private String getFindSql() {
		if (findSql == null) {
			findSql = "select substr(val,idx*?,?) as v,version,idx from " + getTable() + " where day=?";
		}
		return findSql;
	}

	private String getUpdateSql() {
		if (updateSql == null) {
			updateSql = "update " + getTable() + " set idx=idx+?,version=version+1 where day=? and version=?";
		}
		return updateSql;
	}

	protected abstract String getTable();

}
