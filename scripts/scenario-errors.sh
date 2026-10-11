#!/usr/bin/env bash
set -euo pipefail
curl -fsS -X POST 'http://localhost:8081/admin/chaos?errorRate=0.02&slowRate=0.05&slowMs=900' ; echo
curl -fsS -X POST 'http://localhost:8082/admin/chaos?errorRate=0.30&minDelayMs=35&maxDelayMs=220' ; echo
