#!/usr/bin/env bash
set -euo pipefail
curl -fsS -X POST 'http://localhost:8081/admin/chaos?errorRate=0.03&slowRate=0.25&slowMs=2000' ; echo
curl -fsS -X POST 'http://localhost:8082/admin/chaos?errorRate=0.18&minDelayMs=400&maxDelayMs=1400' ; echo
