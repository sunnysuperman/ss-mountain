package com.sunnysuperman.mountain.lang.test.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.sunnysuperman.mountain.lang.utils.Obj;

class ObjTest {

	public static class MyObj {
		String title;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

	}

	public static class Unit1 {
		String name;

		public Unit1() {
			super();
		}

		public Unit1(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class Unit2 {
		String name;

		public Unit2() {
			super();
		}

		public Unit2(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class Unit3 extends Unit1 {

		public Unit3() {
			super();
		}

		public Unit3(String name) {
			super(name);
		}

	}

	public static class Sku {
		Long id;
		Unit1 storageUnit;
		Unit1 weightUnit;
		Unit3 anotherUnit;
		int price;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Unit1 getStorageUnit() {
			return storageUnit;
		}

		public void setStorageUnit(Unit1 storageUnit) {
			this.storageUnit = storageUnit;
		}

		public Unit1 getWeightUnit() {
			return weightUnit;
		}

		public void setWeightUnit(Unit1 weightUnit) {
			this.weightUnit = weightUnit;
		}

		public int getPrice() {
			return price;
		}

		public void setPrice(int price) {
			this.price = price;
		}

		public Unit3 getAnotherUnit() {
			return anotherUnit;
		}

		public void setAnotherUnit(Unit3 anotherUnit) {
			this.anotherUnit = anotherUnit;
		}

	}

	public static class SkuVO {
		Long id;
		Unit2 storageUnit;
		Unit3 weightUnit;
		Unit1 anotherUnit;
		Integer price;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Unit2 getStorageUnit() {
			return storageUnit;
		}

		public void setStorageUnit(Unit2 storageUnit) {
			this.storageUnit = storageUnit;
		}

		public Unit3 getWeightUnit() {
			return weightUnit;
		}

		public void setWeightUnit(Unit3 weightUnit) {
			this.weightUnit = weightUnit;
		}

		public Integer getPrice() {
			return price;
		}

		public void setPrice(Integer price) {
			this.price = price;
		}

		public Unit1 getAnotherUnit() {
			return anotherUnit;
		}

		public void setAnotherUnit(Unit1 anotherUnit) {
			this.anotherUnit = anotherUnit;
		}

	}

	@Test
	void testEquals() {
		assertTrue(Obj.equals("1", "1"));
		assertTrue(Obj.equals("", ""));
		assertTrue(Obj.equals(null, null));

		assertFalse(Obj.equals("1", "2"));
		assertFalse(Obj.equals("1", null));
		assertFalse(Obj.equals("", null));
	}

	@Test
	void testNotEquals() {
		assertFalse(Obj.notEquals("1", "1"));
		assertFalse(Obj.notEquals("", ""));
		assertFalse(Obj.notEquals(null, null));

		assertTrue(Obj.notEquals("1", "2"));
		assertTrue(Obj.notEquals("1", null));
		assertTrue(Obj.notEquals("", null));
	}

	@Test
	void testSetPropertyAndGetProperty() {
		MyObj obj = new MyObj();
		Obj.setProperty(obj, "title", "xx");
		assertEquals("xx", Obj.getProperty(obj, "title"));
	}

	@Test
	void testCopyProperties() {
		{
			Sku sku = new Sku();
			sku.id = 999L;
			sku.storageUnit = new Unit1("盒");
			sku.price = 100;
			SkuVO vo = Obj.copyProperties(sku, new SkuVO());
			assertNull(vo.getStorageUnit());
			assertNull(vo.getWeightUnit());
			assertEquals(999L, vo.getId());
			assertEquals(100, vo.getPrice());
		}
		{
			Sku sku = new Sku();
			sku.weightUnit = new Unit1("盒");
			sku.anotherUnit = new Unit3("KG");
			SkuVO vo = Obj.copyProperties(sku, new SkuVO());
			assertEquals(0, vo.getPrice());
			assertNull(vo.getStorageUnit());
			assertNull(vo.getWeightUnit());
			assertEquals("KG", vo.getAnotherUnit().getName());
			assertEquals(Unit3.class, vo.getAnotherUnit().getClass());
		}
	}

	@Test
	void testCopyNotNullProperties() {
		{
			Sku sku = new Sku();
			sku.id = 999L;
			sku.storageUnit = new Unit1("盒");
			sku.price = 100;
			SkuVO vo = Obj.copyNotNullProperties(sku, new SkuVO());
			assertNull(vo.getStorageUnit());
			assertNull(vo.getWeightUnit());
			assertEquals(999L, vo.getId());
			assertEquals(100, vo.getPrice());
		}
		{
			Sku sku = new Sku();
			sku.weightUnit = new Unit1("盒");
			sku.anotherUnit = new Unit3("KG");
			SkuVO vo = Obj.copyNotNullProperties(sku, new SkuVO());
			assertEquals(0, vo.getPrice());
			assertNull(vo.getStorageUnit());
			assertNull(vo.getWeightUnit());
			assertEquals("KG", vo.getAnotherUnit().getName());
			assertEquals(Unit3.class, vo.getAnotherUnit().getClass());
		}
	}

}
