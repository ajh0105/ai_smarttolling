from __future__ import annotations

import argparse
import asyncio
import logging
import ssl
import time
from pathlib import Path

from hifive_jetson_py.ingress_state import IngressStats
from hifive_jetson_py.spring_forwarder import SpringEvidenceForwarder, SpringForwarder, SpringJsonForwarder
from hifive_jetson_py.srt_video_receiver import SrtVideoReceiver, SrtVideoReceiverOptions
from hifive_jetson_py.webtransport_ingress import WebTransportIngressFactory


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="HI-FIVE WebTransport ingress to Spring REST")
    parser.add_argument("--host", default="0.0.0.0")
    parser.add_argument("--port", type=int, default=4433)
    parser.add_argument("--cert", required=True)
    parser.add_argument("--key", required=True)
    parser.add_argument("--wt-path", default="/hifive/edge")
    parser.add_argument("--spring-url", default="http://127.0.0.1:8080/api/ingest/passage-events")
    parser.add_argument("--spring-edge-status-url", default="")
    parser.add_argument("--spring-ingress-status-url", default="")
    parser.add_argument("--spring-evidence-url", default="")
    parser.add_argument("--ingress-status-forward-interval-sec", type=float, default=0.0)
    parser.add_argument("--spring-timeout-sec", type=float, default=3.0)
    parser.add_argument("--ingest-key", default="")
    parser.add_argument("--dry-run-spring", action="store_true")
    parser.add_argument("--ops-host", default="0.0.0.0")
    parser.add_argument("--ops-port", type=int, default=8000)
    parser.add_argument("--srt-listen-host", default="0.0.0.0")
    parser.add_argument("--srt-listen-port", type=int, default=0)
    parser.add_argument("--srt-latency-ms", type=int, default=120)
    parser.add_argument("--srt-receiver-command", default="ffmpeg")
    parser.add_argument("--srt-hls-dir", default="runtime/video_hls")
    parser.add_argument("--srt-hls-segment-sec", type=float, default=1.0)
    parser.add_argument("--srt-hls-list-size", type=int, default=24)
    parser.add_argument("--srt-hls-delete-threshold", type=int, default=24)
    parser.add_argument("--srt-hls-output-fps", type=int, default=0)
    parser.add_argument("--srt-hls-reencode-bitrate-kbps", type=int, default=1800)
    return parser.parse_args()


def build_ops_app(stats: IngressStats, hls_dir: str | None = None):
    from fastapi import FastAPI
    from fastapi.responses import FileResponse, PlainTextResponse, Response, StreamingResponse

    app = FastAPI(title="HI-FIVE Python Ingress Ops")
    hls_root = Path(hls_dir).resolve() if hls_dir else None

    @app.get("/healthz")
    async def healthz() -> dict[str, str]:
        return {"status": "ok"}

    @app.get("/status")
    async def status() -> dict:
        return stats.snapshot()

    @app.get("/video/status")
    async def video_status() -> dict:
        return stats.snapshot().get("video_receiver", {})

    @app.get("/video/latest.jpg")
    async def latest_video_frame() -> Response:
        payload, meta = stats.get_latest_video_frame()
        if not payload:
            return Response(status_code=404)
        headers = {}
        if meta.get("ts_ms"):
            headers["X-Hifive-Video-Ts-Ms"] = str(meta["ts_ms"])
        return Response(content=payload, media_type="image/jpeg", headers=headers)

    @app.get("/video/stream.mjpg")
    async def video_stream() -> StreamingResponse:
        async def frames():
            last_ts_ms = 0
            stats.mark_video_viewer_open()
            try:
                while True:
                    payload, meta = stats.get_latest_video_frame()
                    ts_ms = int(meta.get("ts_ms", 0) or 0)
                    if payload and ts_ms != last_ts_ms:
                        last_ts_ms = ts_ms
                        header = (
                            b"--frame\r\n"
                            b"Content-Type: image/jpeg\r\n"
                            + f"Content-Length: {len(payload)}\r\n".encode("ascii")
                            + f"X-Hifive-Video-Ts-Ms: {ts_ms}\r\n\r\n".encode("ascii")
                        )
                        yield header + payload + b"\r\n"
                    await asyncio.sleep(0.05)
            finally:
                stats.mark_video_viewer_closed()

        return StreamingResponse(
            frames(),
            media_type="multipart/x-mixed-replace; boundary=frame",
            headers={"Cache-Control": "no-store"},
        )

    @app.get("/video/hls/master.m3u8")
    async def video_hls_playlist() -> Response:
        if hls_root is None:
            return Response(status_code=404)
        path = hls_root / "master.m3u8"
        if not path.is_file():
            return Response(status_code=404)
        return Response(
            content=path.read_bytes(),
            media_type="application/vnd.apple.mpegurl",
            headers={
                "Cache-Control": "no-store, no-cache, must-revalidate, max-age=0",
                "Pragma": "no-cache",
                "Expires": "0",
            },
        )

    @app.get("/video/hls/{segment_name}")
    async def video_hls_segment(segment_name: str) -> Response:
        if hls_root is None:
            return Response(status_code=404)
        if "/" in segment_name or "\\" in segment_name or ".." in segment_name or not segment_name.endswith(".ts"):
            return Response(status_code=404)
        path = hls_root / segment_name
        if not path.is_file():
            return Response(status_code=404)
        return FileResponse(
            path,
            media_type="video/mp2t",
            headers={"Cache-Control": "no-store"},
        )

    @app.get("/preview/latest.jpg")
    async def latest_preview() -> Response:
        payload, meta = stats.get_latest_preview()
        if not payload:
            return Response(status_code=404)
        headers = {}
        if meta.get("event_id"):
            headers["X-Hifive-Preview-Event-Id"] = str(meta["event_id"])
        if meta.get("ts_ms"):
            headers["X-Hifive-Preview-Ts-Ms"] = str(meta["ts_ms"])
        return Response(content=payload, media_type="image/jpeg", headers=headers)

    @app.get("/metrics", response_class=PlainTextResponse)
    async def metrics() -> str:
        snapshot = stats.snapshot()
        lines = []
        for key, value in snapshot.items():
            if isinstance(value, (int, float)):
                lines.append(f"hifive_ingress_{key} {value}")
        return "\n".join(lines) + "\n"

    return app


async def run_ops_api(stats: IngressStats, host: str, port: int, hls_dir: str | None = None) -> None:
    import uvicorn

    config = uvicorn.Config(build_ops_app(stats, hls_dir), host=host, port=port, log_level="info")
    server = uvicorn.Server(config)
    await server.serve()


async def forward_ingress_status_loop(
    stats: IngressStats,
    forwarder: SpringJsonForwarder,
    interval_sec: float,
) -> None:
    interval = max(0.2, interval_sec)
    while True:
        ts_ms = int(time.time() * 1000)
        event_id = f"ingress-status-{ts_ms}"
        snapshot = stats.snapshot()
        snapshot["uptime_sec"] = int(snapshot.get("uptime_sec") or 0)
        payload = {
            "type": "ingress_status",
            "schema_version": "hifive.ingress_status.v1",
            "ts_ms": ts_ms,
            "ingress_id": "python-webtransport-ingress",
            **snapshot,
        }
        result = await forwarder.forward(payload, event_id)
        if result.accepted:
            stats.mark_ingress_status_forward(event_id, "accepted", result.status_code, result.detail)
        elif result.retryable:
            stats.mark_ingress_status_forward(event_id, "retry", result.status_code, result.detail)
        else:
            stats.mark_ingress_status_forward(event_id, "rejected", result.status_code, result.detail)
        await asyncio.sleep(interval)


async def async_main() -> None:
    args = parse_args()
    logging.basicConfig(level=logging.INFO, format="INFO:     %(message)s")

    from aioquic.asyncio import serve
    from aioquic.h3.connection import H3_ALPN
    from aioquic.quic.configuration import QuicConfiguration

    stats = IngressStats()
    srt_receiver = SrtVideoReceiver(
        SrtVideoReceiverOptions(
            host=args.srt_listen_host,
            port=args.srt_listen_port,
            latency_ms=args.srt_latency_ms,
            command=args.srt_receiver_command,
            hls_dir=args.srt_hls_dir,
            hls_segment_seconds=args.srt_hls_segment_sec,
            hls_list_size=args.srt_hls_list_size,
            hls_delete_threshold=args.srt_hls_delete_threshold,
            hls_output_fps=args.srt_hls_output_fps,
            hls_reencode_bitrate_kbps=args.srt_hls_reencode_bitrate_kbps,
        ),
        stats,
    )
    srt_receiver.start()
    forwarder = SpringForwarder(
        endpoint=args.spring_url,
        timeout_sec=args.spring_timeout_sec,
        ingest_key=args.ingest_key,
        dry_run=args.dry_run_spring,
    )
    edge_status_forwarder = None
    if args.spring_edge_status_url or args.dry_run_spring:
        edge_status_forwarder = SpringJsonForwarder(
            endpoint=args.spring_edge_status_url,
            timeout_sec=args.spring_timeout_sec,
            ingest_key=args.ingest_key,
            dry_run=args.dry_run_spring,
        )
    ingress_status_forwarder = None
    if args.spring_ingress_status_url or args.dry_run_spring:
        ingress_status_forwarder = SpringJsonForwarder(
            endpoint=args.spring_ingress_status_url,
            timeout_sec=args.spring_timeout_sec,
            ingest_key=args.ingest_key,
            dry_run=args.dry_run_spring,
        )
    evidence_forwarder = None
    if args.spring_evidence_url or args.dry_run_spring:
        evidence_forwarder = SpringEvidenceForwarder(
            endpoint=args.spring_evidence_url,
            timeout_sec=args.spring_timeout_sec,
            ingest_key=args.ingest_key,
            dry_run=args.dry_run_spring,
        )

    configuration = QuicConfiguration(
        is_client=False,
        alpn_protocols=H3_ALPN,
        verify_mode=ssl.CERT_NONE,
    )
    configuration.max_datagram_frame_size = 65536
    configuration.load_cert_chain(args.cert, args.key)

    await serve(
        args.host,
        args.port,
        configuration=configuration,
        create_protocol=WebTransportIngressFactory(
            path=args.wt_path,
            forwarder=forwarder,
            edge_status_forwarder=edge_status_forwarder,
            evidence_forwarder=evidence_forwarder,
            stats=stats,
        ).create(),
    )
    asyncio.create_task(run_ops_api(stats, args.ops_host, args.ops_port, args.srt_hls_dir))
    if args.ingress_status_forward_interval_sec > 0 and ingress_status_forwarder is not None:
        asyncio.create_task(
            forward_ingress_status_loop(
                stats,
                ingress_status_forwarder,
                args.ingress_status_forward_interval_sec,
            )
        )
    print(f"WebTransport ingress listening on {args.host}:{args.port}{args.wt_path}")
    print(f"Ops API listening on http://{args.ops_host}:{args.ops_port}")
    await asyncio.Future()


def main() -> None:
    asyncio.run(async_main())


if __name__ == "__main__":
    main()
