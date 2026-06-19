package com.hifive.iot.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import com.hifive.iot.dto.PassageEventPayload;

import org.springframework.stereotype.Component;

@Component
public class ProtobufPassageEventDecoder {

	public PassageEventPayload decode(byte[] payload) {
		try {
			Map<Integer, Object> fields = readFields(payload);
			Map<Integer, Object> plate = message(fields, 12);
			Map<Integer, Object> bbox = message(fields, 14);
			return new PassageEventPayload(
				string(fields, 1),
				string(fields, 2),
				string(fields, 3),
				string(fields, 4),
				string(fields, 5),
				timestampString(fields, 10),
				string(fields, 11),
				integer(fields, 6),
				integer(fields, 7),
				string(fields, 8),
				string(fields, 9),
				float64(fields, 20),
				string(plate, 1),
				float64(plate, 2),
				integer(plate, 3),
				float64(plate, 4),
				float64(bbox, 1),
				float64(bbox, 2),
				float64(bbox, 3),
				float64(bbox, 4),
				string(bbox, 6),
				bool(fields, 15),
				string(fields, 16),
				string(fields, 18)
			);
		} catch (IOException | RuntimeException exception) {
			throw new IllegalArgumentException("invalid protobuf or required field missing");
		}
	}

	private Map<Integer, Object> readFields(byte[] payload) throws IOException {
		Map<Integer, Object> fields = new HashMap<>();
		ByteArrayInputStream in = new ByteArrayInputStream(payload);
		while (in.available() > 0) {
			long tag = readVarint(in);
			int number = (int) (tag >> 3);
			int wireType = (int) (tag & 7);
			if (wireType == 0) {
				fields.put(number, readVarint(in));
			} else if (wireType == 1) {
				fields.put(number, readFixed64(in));
			} else if (wireType == 2) {
				int len = (int) readVarint(in);
				byte[] value = in.readNBytes(len);
				fields.put(number, value);
			} else if (wireType == 5) {
				fields.put(number, readFixed32(in));
			} else {
				throw new IOException("unsupported protobuf wire type");
			}
		}
		return fields;
	}

	private long readVarint(ByteArrayInputStream in) throws IOException {
		long result = 0;
		int shift = 0;
		while (shift < 64) {
			int b = in.read();
			if (b < 0) {
				throw new IOException("truncated protobuf varint");
			}
			result |= (long) (b & 0x7F) << shift;
			if ((b & 0x80) == 0) {
				return result;
			}
			shift += 7;
		}
		throw new IOException("malformed protobuf varint");
	}

	private long readFixed64(ByteArrayInputStream in) throws IOException {
		byte[] b = in.readNBytes(8);
		if (b.length != 8) {
			throw new IOException("truncated fixed64");
		}
		return ((long) b[0] & 255) | (((long) b[1] & 255) << 8) | (((long) b[2] & 255) << 16)
			| (((long) b[3] & 255) << 24) | (((long) b[4] & 255) << 32) | (((long) b[5] & 255) << 40)
			| (((long) b[6] & 255) << 48) | (((long) b[7] & 255) << 56);
	}

	private int readFixed32(ByteArrayInputStream in) throws IOException {
		byte[] b = in.readNBytes(4);
		if (b.length != 4) {
			throw new IOException("truncated fixed32");
		}
		return (b[0] & 255) | ((b[1] & 255) << 8) | ((b[2] & 255) << 16) | ((b[3] & 255) << 24);
	}

	private Map<Integer, Object> message(Map<Integer, Object> fields, int key) throws IOException {
		Object value = fields.get(key);
		if (!(value instanceof byte[] bytes)) {
			return Map.of();
		}
		return readFields(bytes);
	}

	private String string(Map<Integer, Object> fields, int key) {
		Object value = fields.get(key);
		if (value instanceof byte[] bytes) {
			return new String(bytes, StandardCharsets.UTF_8);
		}
		return null;
	}

	private Integer integer(Map<Integer, Object> fields, int key) {
		Long value = longValue(fields, key);
		return value == null ? null : value.intValue();
	}

	private Long longValue(Map<Integer, Object> fields, int key) {
		Object value = fields.get(key);
		return value instanceof Number number ? number.longValue() : null;
	}

	private Double float64(Map<Integer, Object> fields, int key) {
		Object value = fields.get(key);
		if (value instanceof Integer fixed32) {
			return (double) Float.intBitsToFloat(fixed32);
		}
		if (value instanceof Long fixed64) {
			return Double.longBitsToDouble(fixed64);
		}
		if (value instanceof Number number) {
			return number.doubleValue();
		}
		return null;
	}

	private Boolean bool(Map<Integer, Object> fields, int key) {
		Long value = longValue(fields, key);
		return value == null ? null : value != 0;
	}

	private LocalDateTime timestamp(Map<Integer, Object> timestamp) {
		Long seconds = longValue(timestamp, 1);
		Integer nanos = integer(timestamp, 2);
		if (seconds == null) {
			return null;
		}
		return LocalDateTime.ofInstant(
			Instant.ofEpochSecond(seconds, nanos == null ? 0 : nanos),
			ZoneId.systemDefault()
		);
	}

	private LocalDateTime timestampString(Map<Integer, Object> fields, int key) {
		String value = string(fields, key);
		if (value == null || value.isBlank()) {
			return null;
		}
		return OffsetDateTime.parse(value).toLocalDateTime();
	}
}
