#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
[ -f .env ] || cp .env.example .env
sudo docker compose up -d --build
printf '\nBase stack started (apps WITHOUT Java agent).\nOrders API: http://localhost:8080\nGrafana:    http://localhost:3000\nPrometheus: http://localhost:9090\n'

