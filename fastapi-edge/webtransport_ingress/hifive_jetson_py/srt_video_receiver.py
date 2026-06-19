from __future__ import annotations

import shutil
import subprocess
import threading
import time
from collections import deque
from dataclasses import dataclass
from pathlib import Path
from typing import Protocol


class VideoReceiverStats(Protocol):
    def mark_video_receiver(self, status: str, detail: dict) -> None: ...


@dataclass(frozen=True)
class SrtVideoReceiverOptions:
    host: str = "0.0.0.0"
    port: int = 0
    latency_ms: int = 120
    command: str = "ffmpeg"
    hls_dir: str = "runtime/video_hls"
    hls_segment_seconds: float = 1.0
    hls_list_size: int = 24
    hls_delete_threshold: int = 24
    hls_output_fps: int = 0
    hls_reencode_bitrate_kbps: int = 1800


class SrtVideoReceiver:
    def __init__(self, options: SrtVideoReceiverOptions, stats: VideoReceiverStats) -> None:
        self.options = options
        self.stats = stats
        self._process: subprocess.Popen | None = None
        self._thread: threading.Thread | None = None
        self._recent_logs: deque[str] = deque(maxlen=20)
        self._hls_dir = Path(options.hls_dir)
        self._prepared_hls_dir = False
        self._stream_generation = 0
        self._last_fps: float | None = None

    def start(self) -> None:
        if self.options.port <= 0:
            self.stats.mark_video_receiver(
                "DISABLED",
                {
                    "enabled": False,
                    "connected": False,
                    "transport": "SRT/H264 -> HLS",
                    "last_error": "",
                },
            )
            return
        self._thread = threading.Thread(target=self._run, name="hifive-srt-video-receiver", daemon=True)
        self._thread.start()

    def _run(self) -> None:
        command_path = shutil.which(self.options.command)
        if command_path is None:
            self.stats.mark_video_receiver(
                "ERROR",
                {
                    "enabled": True,
                    "connected": False,
                    "transport": "SRT/H264 -> HLS",
                    "listen_uri": self._uri(),
                    "recent_logs": list(self._recent_logs),
                    "last_error": f"receiver command not found: {self.options.command}",
                },
            )
            return

        while True:
            returncode = self._run_once(command_path)
            time.sleep(0.1 if returncode == 0 else 0.5)

    def _run_once(self, command_path: str) -> int:
        self._prepare_hls_dir(clear=not self._prepared_hls_dir)
        self._prepared_hls_dir = True
        self._stream_generation += 1
        stream_generation = self._stream_generation
        args = self._ffmpeg_args(command_path)
        try:
            self._process = subprocess.Popen(
                args,
                stdout=subprocess.DEVNULL,
                stderr=subprocess.PIPE,
                bufsize=0,
            )
        except Exception as exc:
            self.stats.mark_video_receiver(
                "ERROR",
                {
                    "enabled": True,
                    "connected": False,
                    "transport": "SRT/H264 -> HLS",
                    "listen_uri": self._uri(),
                    "recent_logs": list(self._recent_logs),
                    "last_error": str(exc),
                },
            )
            return -1

        self.stats.mark_video_receiver(
            "LISTENING",
            {
                "enabled": True,
                "connected": False,
                "transport": "SRT/H264 -> HLS",
                "listen_uri": self._uri(),
                "hlsPlaylistUrl": "/video/hls/master.m3u8",
                "hls_playlist_url": "/video/hls/master.m3u8",
                "streamGeneration": stream_generation,
                "stream_generation": stream_generation,
                "pid": self._process.pid,
                "recent_logs": list(self._recent_logs),
                "last_error": "",
            },
        )

        stderr_thread = threading.Thread(target=self._read_stderr, args=(self._process,), daemon=True)
        stderr_thread.start()
        hls_thread = threading.Thread(
            target=self._watch_hls_progress,
            args=(self._process, stream_generation),
            daemon=True,
        )
        hls_thread.start()

        returncode = self._process.wait()
        if returncode == 0:
            self._process = None
            return returncode

        self.stats.mark_video_receiver(
            "ERROR",
            {
                "enabled": True,
                "connected": False,
                "transport": "SRT/H264 -> HLS",
                "listen_uri": self._uri(),
                "hlsPlaylistUrl": "/video/hls/master.m3u8",
                "hls_playlist_url": "/video/hls/master.m3u8",
                "streamGeneration": stream_generation,
                "stream_generation": stream_generation,
                "pid": self._process.pid,
                "returncode": returncode,
                "recent_logs": list(self._recent_logs),
                "last_error": f"receiver exited code={returncode}",
            },
        )
        self._process = None
        return returncode

    def _read_stderr(self, process: subprocess.Popen) -> None:
        assert process.stderr is not None
        connected = False
        for raw in process.stderr:
            line = raw.decode("utf-8", errors="replace").strip()
            if not line:
                continue
            self._recent_logs.append(line[:500])
            lower = line.lower()
            progress_fps = self._parse_progress_fps(line)
            if progress_fps is not None:
                self._last_fps = progress_fps
            if not connected and ("input #0" in lower or "stream #0" in lower):
                connected = True
                self._mark_running(process.pid, progress_fps)
            elif connected and progress_fps is not None:
                self._mark_running(process.pid, progress_fps)
            elif ("error" in lower or "failed" in lower) and not self._is_transient_receiver_log(lower):
                self.stats.mark_video_receiver(
                    "ERROR",
                    {
                        "enabled": True,
                        "connected": connected,
                        "transport": "SRT/H264 -> HLS",
                        "listen_uri": self._uri(),
                        "hlsPlaylistUrl": "/video/hls/master.m3u8",
                        "hls_playlist_url": "/video/hls/master.m3u8",
                        "streamGeneration": self._stream_generation,
                        "stream_generation": self._stream_generation,
                        "pid": process.pid,
                        "recent_logs": list(self._recent_logs),
                        "last_error": line[:500],
                        "lastError": line[:500],
                    },
                )

    def _watch_hls_progress(self, process: subprocess.Popen, stream_generation: int) -> None:
        playlist = self._hls_dir / "master.m3u8"
        last_mtime_ns = 0
        while process.poll() is None:
            try:
                stat = playlist.stat()
            except OSError:
                time.sleep(0.2)
                continue
            if stat.st_mtime_ns != last_mtime_ns:
                last_mtime_ns = stat.st_mtime_ns
                self._mark_running(process.pid, self._last_fps, stream_generation=stream_generation)
            time.sleep(0.2)

    def _mark_running(
        self,
        pid: int,
        fps: float | None = None,
        *,
        stream_generation: int | None = None,
    ) -> None:
        now_ms = int(time.time() * 1000)
        if fps is None:
            fps = self._last_fps
        detail = {
            "enabled": True,
            "connected": True,
            "streamStatus": "RUNNING",
            "stream_status": "RUNNING",
            "transport": "SRT/H264 -> HLS",
            "listen_uri": self._uri(),
            "hlsPlaylistUrl": "/video/hls/master.m3u8",
            "hls_playlist_url": "/video/hls/master.m3u8",
            "streamGeneration": stream_generation or self._stream_generation,
            "stream_generation": stream_generation or self._stream_generation,
            "pid": pid,
            "recent_logs": list(self._recent_logs),
            "lastFrameTsMs": now_ms,
            "last_frame_ts_ms": now_ms,
            "lastFrameAgeMs": 0,
            "last_frame_age_ms": 0,
            "last_error": "",
            "lastError": "",
        }
        if fps is not None:
            detail["fps"] = round(float(fps), 3)
        self.stats.mark_video_receiver("RUNNING", detail)

    def _parse_progress_fps(self, line: str) -> float | None:
        if "fps=" not in line:
            return None
        value = line.split("fps=", 1)[1].strip().split(" ", 1)[0]
        try:
            return float(value)
        except ValueError:
            return None

    def _is_transient_receiver_log(self, lower: str) -> bool:
        transient_patterns = (
            "pes packet size mismatch",
            "packet corrupt",
            "corrupt input packet",
            "error during demuxing: i/o error",
            "error on srt socket. trying to reconnect",
            "non-existing pps",
            "no frame!",
        )
        return any(pattern in lower for pattern in transient_patterns)

    def _ffmpeg_args(self, command_path: str) -> list[str]:
        hls_time = max(0.2, float(self.options.hls_segment_seconds))
        hls_list_size = max(4, int(self.options.hls_list_size))
        hls_delete_threshold = max(4, int(self.options.hls_delete_threshold))
        video_output_args = self._video_output_args()
        return [
            command_path,
            "-hide_banner",
            "-loglevel",
            "info",
            "-fflags",
            "+genpts+discardcorrupt",
            "-analyzeduration",
            "2000000",
            "-probesize",
            "4000000",
            "-use_wallclock_as_timestamps",
            "1",
            "-i",
            self._uri(),
            "-map",
            "0:v:0",
            "-an",
            *video_output_args,
            "-flush_packets",
            "1",
            "-muxdelay",
            "0",
            "-muxpreload",
            "0",
            "-f",
            "hls",
            "-hls_time",
            str(hls_time),
            "-hls_list_size",
            str(hls_list_size),
            "-hls_delete_threshold",
            str(hls_delete_threshold),
            "-hls_start_number_source",
            "epoch_us",
            "-hls_flags",
            "delete_segments+omit_endlist+program_date_time+independent_segments+temp_file",
            "-hls_segment_filename",
            str(self._hls_dir / "segment_%d.ts"),
            str(self._hls_dir / "master.m3u8"),
        ]

    def _video_output_args(self) -> list[str]:
        output_fps = max(0, int(self.options.hls_output_fps))
        if output_fps <= 0:
            return ["-c:v", "copy"]

        output_fps = min(60, max(1, output_fps))
        bitrate_kbps = max(256, int(self.options.hls_reencode_bitrate_kbps))
        return [
            "-vf",
            f"fps={output_fps}",
            "-c:v",
            "libx264",
            "-preset",
            "ultrafast",
            "-tune",
            "zerolatency",
            "-pix_fmt",
            "yuv420p",
            "-b:v",
            f"{bitrate_kbps}k",
            "-maxrate",
            f"{bitrate_kbps}k",
            "-bufsize",
            f"{bitrate_kbps * 2}k",
            "-g",
            str(output_fps),
            "-keyint_min",
            str(output_fps),
            "-sc_threshold",
            "0",
        ]

    def _uri(self) -> str:
        latency_us = max(20, int(self.options.latency_ms)) * 1000
        return f"srt://{self.options.host}:{self.options.port}?mode=listener&transtype=live&latency={latency_us}"

    def _prepare_hls_dir(self, *, clear: bool) -> None:
        self._hls_dir.mkdir(parents=True, exist_ok=True)
        if not clear:
            return
        for pattern in ("*.m3u8", "*.ts", "*.tmp"):
            for path in self._hls_dir.glob(pattern):
                try:
                    path.unlink()
                except OSError:
                    pass
