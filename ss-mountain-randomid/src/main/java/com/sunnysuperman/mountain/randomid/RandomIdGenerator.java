package com.sunnysuperman.mountain.randomid;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.cache.redis.RedisCacheFactory;
import com.sunnysuperman.mountain.cache.redis.RedisClient;
import com.sunnysuperman.mountain.lang.utils.Dates;
import com.sunnysuperman.mountain.lang.utils.Num;
import com.sunnysuperman.mountain.lang.utils.ProcessUtil;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.lock.LockHelper;
import com.sunnysuperman.mountain.randomid.repository.RandomIdRepository;

/**
 * 随机算法： 按天随机数 = 分段数 + 随机数(最多4位) 如9位的按天随机数 = 5位分段数(Segment) + 4位分段随机数(Random)
 * 分段数: 从0开始到最大分段数，然后打乱，按天持久化存储 分段随机数: 实时生成 拼接分段数和分段随机数:
 * Segment和Random交叉拼接，避免分段数被肉眼甄别，详见RandomIdUtils.concat
 *
 * 生成随机数步骤: 1.系统启动时或定时任务生成(renew)
 * 2.取出指定数量(M)的分段数，然后实时生成指定数量(N)的分段随机数，拼接分段数+分段随机数，得到M*N个随机数 3.写入缓存(Cache)
 * 4.如果可用随机数低于指定数量，告警
 *
 * 取随机数步骤: 1.直接从缓存里随机取，如果有直接返回 2.如果缓存里随机数不够，则调用生成函数生成
 *
 **/
public class RandomIdGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(RandomIdGenerator.class);

	private RandomIdGeneratorConfig config;
	private LockHelper lockHelper;
	private RedisClient redisClient;
	private String cacheKeyPrefix;

	public RandomIdGenerator(RandomIdGeneratorConfig config, LockHelper lockHelper, RedisCacheFactory cacheFactory) {
		config.validate();
		this.config = config;
		this.lockHelper = lockHelper;
		redisClient = new RedisClient(cacheFactory.getPool());
		cacheKeyPrefix = Str.nullToEmpty(cacheFactory.getPrefix()) + "commons_randomid_" + config.getName() + "_";
	}

	/** 生成 **/
	public String generate() {
		Date date = new Date();
		String key = getCacheKey(date);
		for (int i = 1; i <= 3; i++) {
			try {
				String value = redisClient.spop(key);
				if (value != null) {
					return value;
				}
				renew(null);
			} catch (Exception ex) {
				LOG.error(null, ex);
			}
			ProcessUtil.sleep(500);
		}
		return null;
	}

	/** 刷新 **/
	public void refresh() {
		renew(null);
		deleteExpired();
	}

	/** 刷新第二天 **/
	public void refreshNextDay() {
		renew(Dates.addDays(new Date(), 1));
	}

	/** 主动删除昨天的随机数(可依靠redis的过期机制，但考虑到volatile-lru算法，故采取主动删除的策略) **/
	private void deleteExpired() {
		Calendar cal = Dates.getDefaultCalendar();
		// 凌晨1点以后才执行删除，避免潜在的时钟同步问题
		if (cal.get(Calendar.HOUR) >= 1) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
			String key = getCacheKey(cal.getTime());
			boolean ok = redisClient.del(key);
			if (ok && LOG.isWarnEnabled()) {
				LOG.warn(message("昨日过期ID已删除"));
			}
		}
	}

	/** 生成ID池 **/
	private void renew(Date d) {
		lockHelper.tryLock(RandomIdLockKey.GENERATE, config.getName(), () -> {
			Date date = d == null ? new Date() : d;
			String cacheKey = getCacheKey(date);
			LOG.info(message("准备刷新 " + cacheKey));
			long remainingNum = redisClient.scard(cacheKey);
			// 如果存量ID数大于指定量就不生成了
			if (remainingNum >= config.getMinNum()) {
				LOG.info(message("存量ID数大于指定量 " + remainingNum + ">=" + config.getMinNum()));
				return;
			}
			RandomIdRepository idRepo = config.getIdRepository();
			// 随机数位数
			int randomBits = getRandomBitsNum();
			// 随机数一次取几个
			int randomBatchNum = (int) Math.pow(10, randomBits);
			// 分段位数
			int segmentBits = config.getLength() - randomBits;
			// 一次取几个分段
			int segmentsNum = (int) Math.ceil(((double) config.getMinNum() - remainingNum) / randomBatchNum);
			if (segmentsNum < 1) {
				segmentsNum = 1;
			}
			PopSegmentsResult popResult = idRepo.popSegments(segmentBits, segmentsNum, date);
			int addedNum = 0;
			for (String segment : popResult.getSegments()) {
				String[] values = new String[randomBatchNum];
				for (int i = 0; i < randomBatchNum; i++) {
					values[i] = RandomIdUtils.concat(segment, Num.pad(i, randomBits));
				}
				redisClient.sadd(cacheKey, values);
				addedNum += values.length;
				// for GC
				values = null;
			}
			LOG.info(message("生成ID-" + cacheKey + ": " + addedNum + "个"));
			// 如果可用随机数低于指定数量，告警
			alarmIfNecessary(popResult);
		}, 0, 180);
	}

	private int getRandomBitsNum() {
		return Math.min(config.getLength() - 1, 4);
	}

	private void alarmIfNecessary(PopSegmentsResult popResult) {
		// 剩余可用随机数数量 = 分段数数量 * 分段随机数位数
		// 小于50%时需要报警
		if (popResult.getRemainingNum() * Math.pow(10, getRandomBitsNum()) <= Math.pow(10, config.getLength()) * 0.5) {
			String msg = "ID数小于总数50%，请注意潜在风险";
			if (LOG.isWarnEnabled()) {
				LOG.warn(message(msg));
			}
			if (config.getAlarm() != null) {
				config.getAlarm().onMessage(msg);
			}
		}
	}

	private String message(String msg) {
		return String.format("[RandomIdGenerator-%s] %s", config.getName(), msg);
	}

	private String getCacheKey(Date date) {
		int day = RandomIdUtils.date2day(date);
		return cacheKeyPrefix + day;
	}
}
