package com.sunnysuperman.mountain.lang.test.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sunnysuperman.mountain.lang.enums.CodeAwareEnum;
import com.sunnysuperman.mountain.lang.enums.DescAwareEnum;
import com.sunnysuperman.mountain.lang.json.CodeAwareEnumDeserializer;
import com.sunnysuperman.mountain.lang.json.CodeAwareEnumSerializer;
import com.sunnysuperman.mountain.lang.json.DateOnlyDeserializer;
import com.sunnysuperman.mountain.lang.json.DateOnlySerializer;
import com.sunnysuperman.mountain.lang.json.DescAwareEnumDeserializer;
import com.sunnysuperman.mountain.lang.json.DescAwareEnumSerializer;
import com.sunnysuperman.mountain.lang.utils.Dates;
import com.sunnysuperman.mountain.lang.utils.Jsons;

class JsonDeserializerTest {

	@JsonSerialize(using = DescAwareEnumSerializer.class)
	@JsonDeserialize(using = DescAwareEnumDeserializer.class)
	public enum MyStatus implements DescAwareEnum {

		INIT(1, "初始化"),

		COMPLETED(2, "已完成"),

		;

		private byte code;
		private String desc;

		MyStatus(int code, String desc) {
			this.code = (byte) code;
			this.desc = desc;
		}

		@Override
		public byte code() {
			return code;
		}

		@Override
		public String description() {
			return desc;
		}

	}

	@JsonSerialize(using = DescAwareEnumSerializer.class)
	@JsonDeserialize(using = DescAwareEnumDeserializer.class)
	public enum MyAnotherStatus implements DescAwareEnum {

		ING(1, "进行中"),

		SUCCESS(2, "成功"),

		FAIL(3, "失败"),

		;

		private byte code;
		private String desc;

		MyAnotherStatus(int code, String desc) {
			this.code = (byte) code;
			this.desc = desc;
		}

		@Override
		public byte code() {
			return code;
		}

		@Override
		public String description() {
			return desc;
		}

	}

	@JsonSerialize(using = CodeAwareEnumSerializer.class)
	@JsonDeserialize(using = CodeAwareEnumDeserializer.class)
	public enum CodeOnlyStatus implements CodeAwareEnum {

		ING(1),

		SUCCESS(2),

		;

		private byte code;

		CodeOnlyStatus(int code) {
			this.code = (byte) code;
		}

		@Override
		public byte code() {
			return code;
		}

	}

	@JsonSerialize(using = DescAwareEnumSerializer.class)
	@JsonDeserialize(using = DescAwareEnumDeserializer.class)
	public enum MultiMode implements DescAwareEnum {

		MODE1(11, "模式1"),

		MODE2(22, "模式2"),

		MODE3(33, "模式3"),

		;

		private byte code;
		private String desc;

		MultiMode(int code, String desc) {
			this.code = (byte) code;
			this.desc = desc;
		}

		@Override
		public byte code() {
			return code;
		}

		@Override
		public String description() {
			return desc;
		}

	}

	public static class MyObject {
		Integer id;
		MyStatus status;
		MyAnotherStatus anotherStatus;
		CodeOnlyStatus codeStatus;
		List<CodeOnlyStatus> codeStatusList;
		List<MultiMode> modes;

		@JsonSerialize(using = DateOnlySerializer.class)
		@JsonDeserialize(using = DateOnlyDeserializer.class)
		Date date;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public MyStatus getStatus() {
			return status;
		}

		public void setStatus(MyStatus status) {
			this.status = status;
		}

		public MyAnotherStatus getAnotherStatus() {
			return anotherStatus;
		}

		public void setAnotherStatus(MyAnotherStatus anotherStatus) {
			this.anotherStatus = anotherStatus;
		}

		public CodeOnlyStatus getCodeStatus() {
			return codeStatus;
		}

		public void setCodeStatus(CodeOnlyStatus codeStatus) {
			this.codeStatus = codeStatus;
		}

		public List<CodeOnlyStatus> getCodeStatusList() {
			return codeStatusList;
		}

		public void setCodeStatusList(List<CodeOnlyStatus> codeStatusList) {
			this.codeStatusList = codeStatusList;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public List<MultiMode> getModes() {
			return modes;
		}

		public void setModes(List<MultiMode> modes) {
			this.modes = modes;
		}

	}

	@Test
	void testDescAwareEnum() {
		for (int i = 0; i < 2; i++) {
			{
				MyObject obj = new MyObject();
				obj.status = MyStatus.COMPLETED;

				String s = Jsons.write(obj);
				assertEquals("{\"status\":{\"code\":2,\"desc\":\"已完成\"}}", s);

				MyObject obj2 = Jsons.read(s, MyObject.class);
				assertSame(MyStatus.COMPLETED, obj2.status);
				assertNull(obj2.getModes());
			}
			{
				MyObject obj = new MyObject();
				obj.anotherStatus = MyAnotherStatus.SUCCESS;

				String s = Jsons.write(obj);
				assertEquals("{\"anotherStatus\":{\"code\":2,\"desc\":\"成功\"}}", s);

				MyObject obj2 = Jsons.read(s, MyObject.class);
				assertSame(MyAnotherStatus.SUCCESS, obj2.anotherStatus);
			}
			{
				MyObject obj = new MyObject();
				obj.anotherStatus = MyAnotherStatus.SUCCESS;
				obj.modes = Arrays.asList(MultiMode.MODE3, MultiMode.MODE1);

				String s = Jsons.write(obj);

				MyObject obj2 = Jsons.read(s, MyObject.class);
				assertSame(MyAnotherStatus.SUCCESS, obj2.anotherStatus);
				assertSame(MultiMode.MODE3, obj2.modes.get(0));
				assertSame(MultiMode.MODE1, obj2.modes.get(1));
			}
		}
	}

	@Test
	void testCodeAwareEnum() {
		for (int i = 0; i < 2; i++) {
			MyObject obj = new MyObject();
			obj.codeStatus = CodeOnlyStatus.SUCCESS;

			String s = Jsons.write(obj);
			assertEquals("{\"codeStatus\":2}", s);

			MyObject obj2 = Jsons.read(s, MyObject.class);
			assertSame(CodeOnlyStatus.SUCCESS, obj2.codeStatus);
		}

		MyObject obj = new MyObject();
		obj.codeStatusList = List.of(CodeOnlyStatus.SUCCESS, CodeOnlyStatus.ING);
		String s = Jsons.write(obj);
		MyObject obj2 = Jsons.read(s, MyObject.class);
		assertEquals(2, obj2.getCodeStatusList().size());
		assertEquals(CodeOnlyStatus.SUCCESS, obj2.getCodeStatusList().get(0));
		assertEquals(CodeOnlyStatus.ING, obj2.getCodeStatusList().get(1));
	}

	@Test
	void testDate() {
		for (int i = 0; i < 2; i++) {
			MyObject obj = new MyObject();
			Calendar cal = Dates.getDefaultCalendar(new Date());
			Dates.clearDateTime(cal);
			obj.date = cal.getTime();

			String s = Jsons.write(obj);

			MyObject obj2 = Jsons.read(s, MyObject.class);
			assertEquals(obj.date, obj2.date);
		}
	}

}
