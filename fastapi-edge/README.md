# hifive Python Ingress

Python Ingress receives Edge passage events over WebTransport over QUIC/TLS and forwards protobuf bytes to Spring Boot REST ingest.

Current responsibilities:

- Provide operational endpoints: `/healthz`, `/status`, `/metrics`
- Forward protobuf passage events to Spring Boot
- Return ACK / RETRY / REJECT semantics based on Spring response

Not responsibilities:

- Final DB persistence decisions
- VehiclePass fusion
- toll calculation
- review state decisions
- Vue-facing API
- GPS telemetry ingestion

Spring Boot owns the business boundary.

```text
Jetson Edge
-> WebTransport over QUIC/TLS
-> Python Ingress
-> POST /api/ingest/passage-events
-> Spring Boot / PostgreSQL
```

GPS telemetry goes directly to Spring Boot:

```text
Vehicle GPS / OBU
-> POST /api/gps/telemetry
-> gps_telemetry
```
