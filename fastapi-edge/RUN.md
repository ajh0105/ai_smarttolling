# FastAPI Python Ingress 실행 가이드

Python Ingress는 Edge와 Spring Boot 사이의 전송 어댑터다. 최종 검증, 중복 처리, 저장, 정산, 검수는 Spring Boot가 담당한다.

## 실행

```bash
cd fastapi-edge
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

## 운영 확인 API

```bash
curl http://localhost:8000/healthz
curl http://localhost:8000/status
curl http://localhost:8000/metrics
```

## 개발용 전달 테스트

실제 운영 전송은 WebTransport over QUIC/TLS 기준이다. 아래 HTTP endpoint는 Spring ingest 계약을 로컬에서 확인하기 위한 개발용 브리지다.

```bash
curl -X POST http://localhost:8000/internal/passage-events \
  -H "Content-Type: application/x-protobuf" \
  -H "X-Event-Id: edge-local-0001" \
  --data-binary "@sample.bin"
```

Spring Boot는 다음 endpoint로 protobuf bytes를 수신한다.

```text
POST /api/ingest/passage-events
Content-Type: application/x-protobuf
X-Event-Id: {event_id}
```

GPS telemetry는 Python Ingress를 거치지 않고 Spring Boot가 직접 받는다.

```text
POST /api/gps/telemetry
GET  /api/gps/telemetry/latest
```
