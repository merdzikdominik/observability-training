#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
docker compose -f compose.yaml -f compose.otel.yaml stop orders-api inventory-api payment-api
docker compose up -d --force-recreate orders-api inventory-api payment-api
printf '\nApplications restarted without Java Agent.\n'
