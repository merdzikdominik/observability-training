#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
docker compose --profile tools run --rm k6 run /scripts/high.js
