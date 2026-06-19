"""Environment based settings for the Python Ingress server."""
from functools import lru_cache

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=".env",
        env_prefix="EDGE_",
        case_sensitive=False,
    )

    edge_node_id: str = Field(default="EDGE-LOCAL-01")

    # WebTransport over QUIC/TLS is the production edge transport.
    webtransport_host: str = Field(default="0.0.0.0")
    webtransport_port: int = Field(default=8443)
    webtransport_cert_path: str | None = Field(default=None)
    webtransport_key_path: str | None = Field(default=None)

    # Python Ingress forwards accepted protobuf bytes to Spring Boot.
    spring_rest_base_url: str = Field(default="http://localhost:8585")
    spring_rest_timeout_sec: float = Field(default=2.0)


@lru_cache
def get_settings() -> Settings:
    return Settings()
