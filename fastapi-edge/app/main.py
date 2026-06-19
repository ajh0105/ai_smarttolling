"""Python Ingress entrypoint.

The ingress process is an edge transport adapter. Business validation,
deduplication, persistence, tolling, review, and Vue-facing APIs live in
Spring Boot.
"""
from __future__ import annotations

import logging
from contextlib import asynccontextmanager
from time import monotonic

from fastapi import FastAPI

from app.api.routes import router as api_router
from app.core.config import get_settings
from app.services.spring_client import SpringRestClient

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s :: %(message)s",
)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    settings = get_settings()
    app.state.settings = settings
    app.state.started_at = monotonic()
    app.state.forwarded_events = 0
    app.state.spring_client = SpringRestClient(settings)
    logger.info("hifive Python Ingress ready (node=%s)", settings.edge_node_id)
    yield


app = FastAPI(
    title="hifive Python Ingress",
    description="WebTransport ingress adapter for protobuf passage events",
    version="0.2.0",
    lifespan=lifespan,
)
app.include_router(api_router)
