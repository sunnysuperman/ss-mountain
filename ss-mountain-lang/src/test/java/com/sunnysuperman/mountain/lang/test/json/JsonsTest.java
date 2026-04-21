package com.sunnysuperman.mountain.lang.test.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.pagination.PullPage;
import com.sunnysuperman.mountain.lang.utils.Jsons;

import lombok.Getter;
import lombok.Setter;

class JsonsTest {
	private static final Logger LOG = LoggerFactory.getLogger(JsonsTest.class);
	static List<User> sUsers;

	@BeforeAll
	static void init() {
		sUsers = IntStream.range(1, 101).mapToObj(i -> new User(i, "用户" + i)).collect(Collectors.toList());
	}

	@Test
	void testReadInParallel() {
		int loop = 100;
		ExecutorService pool = Executors.newFixedThreadPool(10);
		String userJSON = Jsons.write(sUsers.get(0));
		String usersJSON = Jsons.write(sUsers);
		AtomicInteger completedCount = new AtomicInteger(0);

		List<Runnable> tasks = IntStream.range(0, loop).mapToObj(index -> (Runnable) () -> {
			// 转对象
			{
				User user = Jsons.read(userJSON, User.class);
				assertEquals(sUsers.get(0).getId(), user.getId());
				assertEquals(sUsers.get(0).getName(), user.getName());
				assertNull(user.getComment());
			}

			// 转Map
			{
				Map<String, Object> userMap = Jsons.readForMap(userJSON);
				assertEquals(sUsers.get(0).getId(), userMap.get("id"));
				assertEquals(sUsers.get(0).getName(), userMap.get("name"));
				assertNull(userMap.get("comment"));
			}
			{
				@SuppressWarnings("unchecked")
				Map<String, Object> userMap = (Map<String, Object>) Jsons.read(userJSON, Map.class);
				assertEquals(sUsers.get(0).getId(), userMap.get("id"));
				assertEquals(sUsers.get(0).getName(), userMap.get("name"));
				assertNull(userMap.get("comment"));
			}

			// 转对象列表
			{
				List<User> userList = Jsons.readForList(usersJSON, User.class);
				assertEquals(sUsers.size(), userList.size());
				for (int i = 0; i < userList.size(); i++) {
					assertEquals(sUsers.get(i).getId(), userList.get(i).getId());
					assertEquals(sUsers.get(i).getName(), userList.get(i).getName());
				}
			}

			// 转Map列表
			{
				List<Map<String, Object>> userMapList = Jsons.readForMapList(usersJSON);
				assertEquals(sUsers.size(), userMapList.size());
				for (int i = 0; i < userMapList.size(); i++) {
					assertEquals(sUsers.get(i).getId(), userMapList.get(i).get("id"));
					assertEquals(sUsers.get(i).getName(), userMapList.get(i).get("name"));
				}
			}
			{
				@SuppressWarnings("rawtypes")
				List<Map> userMapList = Jsons.readForList(usersJSON, Map.class);
				assertEquals(sUsers.size(), userMapList.size());
				for (int i = 0; i < userMapList.size(); i++) {
					assertEquals(sUsers.get(i).getId(), userMapList.get(i).get("id"));
					assertEquals(sUsers.get(i).getName(), userMapList.get(i).get("name"));
				}
			}

			// 转数组
			{
				User[] userArray = Jsons.readForArray(usersJSON, User.class);
				assertEquals(sUsers.size(), userArray.length);
				for (int i = 0; i < userArray.length; i++) {
					assertEquals(sUsers.get(i).getId(), userArray[i].getId());
					assertEquals(sUsers.get(i).getName(), userArray[i].getName());
				}
			}

			completedCount.incrementAndGet();
		}).collect(Collectors.toList());

		runInParallel(pool, tasks);
		assertEquals(loop, completedCount.get());
		pool.shutdown();
	}

	@Test
	void testWriteInParallel() {
		int loop = 20;
		ExecutorService pool = Executors.newFixedThreadPool(10);
		CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
		List<Runnable> tasks = IntStream.range(0, loop).mapToObj(index -> (Runnable) () -> {
			String usersJSON = Jsons.write(sUsers);
			list.add(usersJSON);
		}).collect(Collectors.toList());
		runInParallel(pool, tasks);

		assertEquals(loop, list.size());
		for (int i = 1; i < list.size(); i++) {
			assertEquals(list.get(0), list.get(i));
		}

		pool.shutdown();
	}

	@Test
	void testReadForListPerformance() throws IOException {
		String s = Jsons
				.write(IntStream.range(0, 100).mapToObj(i -> new User(i, "用户" + i)).collect(Collectors.toList()));
		int loop = 10_000;
		long timeConsuming1;
		{
			long t1 = System.nanoTime();
			for (int i = 0; i < loop; i++) {
				Jsons.readForList(s, User.class);
			}
			long t2 = System.nanoTime();
			timeConsuming1 = TimeUnit.NANOSECONDS.toMillis(t2 - t1);
			LOG.info("耗时:{}ms", timeConsuming1);
		}
		long timeConsuming2;
		{
			long t1 = System.nanoTime();
			for (int i = 0; i < loop; i++) {
				Jsons.getReadMapper().readerForListOf(User.class).readValue(s);
			}
			long t2 = System.nanoTime();
			timeConsuming2 = TimeUnit.NANOSECONDS.toMillis(t2 - t1);
			LOG.info("耗时:{}ms", timeConsuming2);
		}
		assertTrue(timeConsuming1 < timeConsuming2);
	}

	@Test
	void testReadForMapPerformance() throws IOException {
		String s = Jsons
				.write(IntStream.range(0, 100).mapToObj(i -> new User(i, "用户" + i)).collect(Collectors.toList()));
		int loop = 10_000;
		long timeConsuming1;
		{
			long t1 = System.nanoTime();
			for (int i = 0; i < loop; i++) {
				Jsons.readForMapList(s);
			}
			long t2 = System.nanoTime();
			timeConsuming1 = TimeUnit.NANOSECONDS.toMillis(t2 - t1);
			LOG.info("耗时:{}ms", timeConsuming1);
		}
		long timeConsuming2;
		{
			long t1 = System.nanoTime();
			for (int i = 0; i < loop; i++) {
				Jsons.getReadMapper().readValue(s, new TypeReference<List<Map<String, Object>>>() {
				});
			}
			long t2 = System.nanoTime();
			timeConsuming2 = TimeUnit.NANOSECONDS.toMillis(t2 - t1);
			LOG.info("耗时:{}ms", timeConsuming2);
		}
		assertTrue(timeConsuming1 < timeConsuming2);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testReadField() throws IOException, NoSuchFieldException, SecurityException {
		User user = new User();
		user.setFriends(List.of(sUsers.get(1), sUsers.get(3)));
		user.setInterests(List.of(Map.of("id", 10, "name", "篮球"), Map.of("id", 20, "name", "游泳")));
		user.setTags(Set.of("技术大拿", "旅游爱好者"));
		user.setPayload(Map.of("city", "SH"));
		user.setValues(new int[] { 1, 3, 5 });
		user.setRelatives(new User[] { sUsers.get(2), sUsers.get(6) });
		{
			Field field = User.class.getDeclaredField("friends");
			List<User> friends = (List<User>) Jsons.read(Jsons.write(user.getFriends()), field);
			assertEquals(2, friends.size());
			assertEquals(sUsers.get(1).getId(), friends.get(0).getId());
			assertEquals(sUsers.get(3).getName(), friends.get(1).getName());
		}
		{
			Field field = User.class.getDeclaredField("interests");
			List<Map<String, Object>> interests = (List<Map<String, Object>>) Jsons
					.read(Jsons.write(user.getInterests()), field);
			assertEquals(2, interests.size());
			assertEquals(10, interests.get(0).get("id"));
			assertEquals("游泳", interests.get(1).get("name"));
		}
		{
			Field field = User.class.getDeclaredField("tags");
			Set<String> tags = (Set<String>) Jsons.read(Jsons.write(user.getTags()), field);
			assertEquals(2, tags.size());
			assertTrue(tags.contains("技术大拿"));
			assertTrue(tags.contains("旅游爱好者"));
		}
		{
			Field field = User.class.getDeclaredField("payload");
			Map<String, Object> payload = (Map<String, Object>) Jsons.read(Jsons.write(user.getPayload()), field);
			assertEquals("SH", payload.get("city"));
		}
		{
			Field field = User.class.getDeclaredField("values");
			int[] values = (int[]) Jsons.read(Jsons.write(user.getValues()), field);
			assertEquals(3, values.length);
			assertEquals(5, values[2]);
		}
		{
			Field field = User.class.getDeclaredField("relatives");
			User[] relatives = (User[]) Jsons.read(Jsons.write(user.getRelatives()), field);
			assertEquals(2, relatives.length);
			assertEquals(sUsers.get(2).getId(), relatives[0].getId());
			assertEquals(sUsers.get(6).getName(), relatives[1].getName());
		}
	}

	@Test
	void testReader() {
		int loop = 100;
		ExecutorService pool = Executors.newFixedThreadPool(10);
		PullPage<User> page = PullPage.of(List.of(sUsers.get(1), sUsers.get(3)), "3");
		String s = Jsons.write(page);
		AtomicInteger completedCount = new AtomicInteger(0);
		ObjectReader reader = Jsons.getReader(PullPage.class, User.class);

		List<Runnable> tasks = IntStream.range(0, loop).mapToObj(index -> (Runnable) () -> {
			try {
				{
					PullPage<User> page1 = reader.readValue(Jsons.writeAsBytes(page));
					assertTrue(page1.isHasMore());
					assertEquals(page.getContent().size(), page1.getContent().size());
					assertEquals(s, Jsons.write(page1));
				}
				{
					PullPage<User> page1 = reader.readValue(Jsons.write(page));
					assertEquals(s, Jsons.write(page1));
				}
				completedCount.incrementAndGet();
			} catch (Exception ex) {
				throw new UnexpectedException(ex);
			}
		}).collect(Collectors.toList());

		runInParallel(pool, tasks);
		assertEquals(loop, completedCount.get());
		pool.shutdown();
	}

	@SuppressWarnings("rawtypes")
	private void runInParallel(ExecutorService executor, List<Runnable> tasks) {
		CompletableFuture[] futures = tasks.stream().map(task -> CompletableFuture.runAsync(task, executor))
				.collect(Collectors.toList()).toArray(CompletableFuture[]::new);
		CompletableFuture future = CompletableFuture.allOf(futures);
		future.join();
	}

	@Getter
	@Setter
	public static class User {

		int id;

		String name;

		String comment;

		List<User> friends;

		List<Map<String, Object>> interests;

		Set<String> tags;

		Map<String, Object> payload;

		int[] values;

		User[] relatives;

		public User(int id, String name) {
			super();
			this.id = id;
			this.name = name;
		}

		public User() {
			super();
		}

	}

}
