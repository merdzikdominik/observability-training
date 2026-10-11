#!/usr/bin/env bash
set -euo pipefail
command -v docker >/dev/null || { echo 'ERROR: docker not found'; exit 1; }
docker info >/dev/null || { echo 'ERROR: Docker daemon unavailable or user lacks permission'; exit 1; }
docker compose version >/dev/null || { echo 'ERROR: docker compose plugin not found'; exit 1; }
for p in 3000 3100 3200 4317 4318 5001 5432 8080 8081 8082 8889 9090 9093 9101; do
  if ss -ltn 2>/dev/null | awk '{print $4}' | grep -Eq "(^|:)${p}$"; then
    echo "WARN: port ${p} appears to be in use"
  fi
done
echo 'Preflight complete.'
