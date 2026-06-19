package com.hifive.iot.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class GpsTelemetrySchemaInitializer implements ApplicationRunner {

	private final JdbcTemplate jdbcTemplate;

	public GpsTelemetrySchemaInitializer(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void run(ApplicationArguments args) {
		jdbcTemplate.execute("ALTER TABLE IF EXISTS gps_telemetry ALTER COLUMN latitude DROP NOT NULL");
		jdbcTemplate.execute("ALTER TABLE IF EXISTS gps_telemetry ALTER COLUMN longitude DROP NOT NULL");
		jdbcTemplate.execute(
			"ALTER TABLE IF EXISTS gps_telemetry "
				+ "ADD COLUMN IF NOT EXISTS fix_status varchar(20) NOT NULL DEFAULT 'FIXED'"
		);
		jdbcTemplate.execute(
			"ALTER TABLE IF EXISTS gps_telemetry "
				+ "ADD COLUMN IF NOT EXISTS status_message varchar(120)"
		);
		jdbcTemplate.execute(
			"ALTER TABLE IF EXISTS gps_telemetry "
				+ "ADD COLUMN IF NOT EXISTS raw_sentence varchar(180)"
		);
		jdbcTemplate.execute("UPDATE gps_telemetry SET fix_status = 'FIXED' WHERE fix_status IS NULL");
		jdbcTemplate.execute(
			"ALTER TABLE IF EXISTS edge_status_latest "
				+ "ADD COLUMN IF NOT EXISTS dtype varchar(31) NOT NULL DEFAULT 'EdgeStatusLatest'"
		);
		jdbcTemplate.execute(
			"ALTER TABLE IF EXISTS passage_event "
				+ "ALTER COLUMN local_track_id TYPE varchar(80) USING local_track_id::text"
		);
		jdbcTemplate.execute("""
			DO $$
			BEGIN
				IF EXISTS (
					SELECT 1
					FROM information_schema.columns
					WHERE table_name = 'passage_event'
						AND column_name = 'payload_bytes'
						AND udt_name = 'oid'
				) THEN
					BEGIN
						ALTER TABLE passage_event
						ALTER COLUMN payload_bytes TYPE bytea
						USING CASE WHEN payload_bytes IS NULL THEN NULL ELSE lo_get(payload_bytes) END;
					EXCEPTION WHEN OTHERS THEN
						ALTER TABLE passage_event
						ALTER COLUMN payload_bytes TYPE bytea
						USING decode('', 'hex');
					END;
				END IF;
			END $$;
			""");
	}
}
