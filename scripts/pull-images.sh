#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
[ -f .env ] || cp .env.example .env
set -a; source .env; set +a
images=(
  "postgres:${POSTGRES_VERSION}"
  "otel/opentelemetry-collector-contrib:${OTEL_COLLECTOR_VERSION}"
  "prom/prometheus:${PROMETHEUS_VERSION}"
  "prom/alertmanager:${ALERTMANAGER_VERSION}"
  "grafana/grafana:${GRAFANA_VERSION}"
  "grafana/loki:${LOKI_VERSION}"
  "grafana/tempo:${TEMPO_VERSION}"
  "grafana/k6:${K6_VERSION}"
  "maven:3.9.11-eclipse-temurin-21"
  "eclipse-temurin:21-jre"
  "python:3.13-alpine"
)
for image in "${images[@]}"; do
  echo "Pulling $image"
  docker pull "$image"
done
echo 'Images cached. Next: docker compose build.'
