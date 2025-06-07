package com.sunnysuperman.mountain.evt;

import java.util.Date;

import com.sunnysuperman.mountain.lang.utils.Dates;
import com.sunnysuperman.mountain.lang.utils.Num;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.lang.utils.Types;
import com.sunnysuperman.mountain.repository.annotation.Column;
import com.sunnysuperman.mountain.repository.annotation.Id;
import com.sunnysuperman.mountain.repository.annotation.IdStrategy;
import com.sunnysuperman.mountain.repository.annotation.Index;
import com.sunnysuperman.mountain.repository.annotation.Table;
import com.sunnysuperman.mountain.repository.annotation.VersionControl;

@Table(name = Str.EMPTY, indexes = { @Index(columns = { "_scheduled_time" }) })
public abstract class Evt {

	@Id(strategy = IdStrategy.INCREMENT)
	@Column
	private Long id;

	@VersionControl
	@Column(comment = "版本号")
	private Long version;

	@Column(updatable = false, nullable = false, comment = "创建时间")
	private Date createdDate;

	@Column(nullable = false, comment = "修改时间")
	private Date updatedDate;

	@Column(nullable = false, converter = EvtScheduledTimeConverter.class, columnDefinition = {
			"`scheduled_time` BIGINT NOT NULL COMMENT '预约执行时间'",
			"`_scheduled_time` VARCHAR(38) NOT NULL COMMENT '预约执行时间(排序用)'" })
	private Date scheduledTime;

	@Column(nullable = false, comment = "已执行次数")
	private Integer times;

	@Column(updatable = false, length = 3000, comment = "错误消息")
	private String errMsg;

	protected Evt() {
		super();
	}

	protected Evt(Long id) {
		super();
		this.id = id;
	}

	public static <T extends Evt> T of(Class<T> clazz) {
		T instance = Types.newInstance(clazz);
		instance.setVersion(Num.LONG_1);
		instance.setCreatedDate(new Date());
		instance.setUpdatedDate(instance.getCreatedDate());
		instance.setScheduledTime(instance.getCreatedDate());
		instance.setTimes(0);
		return instance;
	}

	boolean scheduleNext() {
		return scheduleNext(null);
	}

	boolean scheduleNext(Date scheduledTime) {
		updatedDate = new Date();
		times++;
		// 预约下次时间
		if (scheduledTime == null) {
			this.scheduledTime = computeScheduledTime(times, updatedDate);
		} else {
			this.scheduledTime = scheduledTime;
		}
		return this.scheduledTime != null;
	}

	/**
	 * 计算下次执行时间
	 * 
	 * @return 下次执行时间，如果下次执行时间为空，表示不再执行，任务会被删除
	 **/
	protected Date computeScheduledTime(int times, Date updatedDate) {
		if (times <= 3) {
			return Dates.addMinutes(updatedDate, times);
		} else if (times <= 8) {
			return Dates.addMinutes(updatedDate, 10);
		} else if (times <= 10) {
			return Dates.addHours(updatedDate, 1);
		} else {
			return Dates.addHours(updatedDate, 8);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Date getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(Date scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

}
