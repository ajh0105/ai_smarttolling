package com.hifive.iot.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

final class StatusValue {
	private StatusValue() {
	}

	static String text(Map<String, Object> map, String key) {
		Object value = map == null ? null : map.get(key);
		return value == null ? null : value.toString();
	}

	static Long lng(Map<String, Object> map, String key) {
		Object value = map == null ? null : map.get(key);
		if (value instanceof Number number) {
			return number.longValue();
		}
		return value == null ? null : Long.valueOf(value.toString());
	}

	static Integer integer(Map<String, Object> map, String key) {
		Long value = lng(map, key);
		return value == null ? null : value.intValue();
	}

	static Double decimal(Map<String, Object> map, String key) {
		Object value = map == null ? null : map.get(key);
		if (value instanceof Number number) {
			return number.doubleValue();
		}
		return value == null ? null : Double.valueOf(value.toString());
	}

	static Boolean bool(Map<String, Object> map, String key) {
		Object value = map == null ? null : map.get(key);
		if (value instanceof Boolean bool) {
			return bool;
		}
		return value == null ? null : Boolean.valueOf(value.toString());
	}

	static LocalDateTime epochMillis(Long millis) {
		return millis == null
			? LocalDateTime.now()
			: LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
	}
}
