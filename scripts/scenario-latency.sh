#!/usr/bin/env bash
set -euo pipefail
curl -fsS -X POST 'http://localhost:8081/admin/chaos?errorRate=0.01&slowRate=0.35&slowMs=1800' ; echo
curl -fsS -X POST 'http://localhost:8082/admin/chaos?errorRate=0.03&minDelayMs=50&maxDelayMs=300' ; echo
