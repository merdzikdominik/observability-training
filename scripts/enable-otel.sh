#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
docker compose -f compose.yaml -f compose.otel.yaml up -d --build --force-recreate orders-api inventory-api payment-api
printf '\nOpenTelemetry Java Agent enabled for all three services.\n'
