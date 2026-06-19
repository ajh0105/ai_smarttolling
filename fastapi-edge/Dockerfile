FROM python:3.11-slim

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends \
        curl \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt ./
COPY webtransport_ingress/requirements_ingress.txt ./requirements_ingress.txt
RUN pip install --no-cache-dir -r requirements.txt \
    && pip install --no-cache-dir -r requirements_ingress.txt

COPY . .

EXPOSE 8000

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
