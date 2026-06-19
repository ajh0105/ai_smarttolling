"""Generate Python protobuf classes for PassageEvent."""
from __future__ import annotations

import subprocess
import sys
from pathlib import Path


def main() -> None:
    root = Path(__file__).resolve().parents[1]
    proto_dir = root / "proto"
    out_dir = root / "app" / "protobuf"
    proto_file = proto_dir / "passage_event.proto"

    out_dir.mkdir(parents=True, exist_ok=True)
    (out_dir / "__init__.py").touch()

    subprocess.check_call([
        sys.executable,
        "-m",
        "grpc_tools.protoc",
        f"-I{proto_dir}",
        f"--python_out={out_dir}",
        str(proto_file),
    ])
    print(f"generated protobuf classes in {out_dir}")


if __name__ == "__main__":
    main()
