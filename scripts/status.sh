#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
docker compose ps
printf '\nCollector health: '; curl -fsS http://localhost:13133/ >/dev/null && echo OK || echo FAIL
