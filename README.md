# Observability Training Lab

Gotowa aplikacja demonstracyjna i lokalny stack observability do 3-dniowego szkolenia.

## Co jest w środku

Aplikacja biznesowa nie zawiera OpenTelemetry SDK ani kodu tworzącego spany/metryki. Instrumentacja aplikacji Java jest uruchamiana z zewnątrz przez OpenTelemetry Java Agent.

Serwisy:
- `orders-api` (Spring Boot + PostgreSQL) — port 8080
- `inventory-api` — port 8081
- `payment-api` — port 8082

Stack:
- OpenTelemetry Collector
- Prometheus
- Grafana
- Tempo
- Loki
- Alertmanager
- prosty webhook do ćwiczeń alertowych
- k6 jako opcjonalny generator ruchu
- `cardinality-demo` — celowo zła metryka z `user_id`, używana wyłącznie do ćwiczeń z kardynalności

## Wymagania VM

Rekomendowane minimum:
- Ubuntu/Debian Linux
- 4 vCPU
- 8 GB RAM
- 25+ GB dysku
- Docker Engine
- Docker Compose plugin (`docker compose`)

Docker Desktop nie jest potrzebny.

## Przygotowanie przed szkoleniem

Na obrazie VM warto wykonać wcześniej:

```bash
./scripts/preflight.sh
./scripts/pull-images.sh
docker compose build
```

Dzięki temu laboratorium nie zależy od pobierania dużych obrazów podczas zajęć.

## 1. Start bez instrumentacji

```bash
cp .env.example .env
./scripts/start-base.sh
```

Sprawdzenie:

```bash
curl http://localhost:8080/api/catalog/sku-1
curl -X POST http://localhost:8080/api/orders \
  -H 'content-type: application/json' \
  -d '{"productId":"sku-1","quantity":1,"amountCents":2599,"customerEmail":"alice@example.com"}'
```

UI:
- Grafana: http://localhost:3000 (admin/admin; anonymous Viewer również włączony)
- Prometheus: http://localhost:9090
- Tempo API: http://localhost:3200
- Loki API: http://localhost:3100
- Alertmanager: http://localhost:9093

## 2. Włącz zero-code Java Agent

```bash
./scripts/enable-otel.sh
```

Aplikacja nadal ma ten sam kod. Zmienia się wyłącznie sposób uruchomienia JVM (`-javaagent`).

Wygeneruj ruch:

```bash
./scripts/load-normal.sh
```

## 3. Scenariusze awarii

```bash
./scripts/scenario-latency.sh
./scripts/scenario-errors.sh
./scripts/scenario-mixed.sh
./scripts/scenario-reset.sh
```

Scenariusze działają przez techniczne endpointy `/admin/chaos` i nie wymagają restartu kontenerów.

## 4. Edycja konfiguracji

Wszystkie konfiguracje są na filesystemie VM:

```text
otel/config.yaml
prometheus/prometheus.yml
prometheus/rules.yml
tempo/tempo.yaml
loki/loki.yaml
alertmanager/alertmanager.yml
grafana/provisioning/...
```

Przykład:

```bash
vim otel/config.yaml
docker compose restart otel-collector
docker compose logs -f otel-collector
```

Analogicznie dla Prometheusa:

```bash
vim prometheus/prometheus.yml
docker compose restart prometheus
```

## 5. Logi i korelacja

Aplikacje zapisują logi w logfmt do wspólnego volume. Collector używa `filelog` receiverów i wysyła je do Loki. Po włączeniu Java Agenta `trace_id` i `span_id` są automatycznie wstrzykiwane do MDC przez agent i pojawiają się w linii logu — bez kodu OpenTelemetry w aplikacji.

Przykładowe LogQL:

```logql
{service_name="payment-api"} | logfmt | level="ERROR"
```

## 6. eBPF / Beyla

Beyla/OBI jest celowo poza podstawowym Compose — eBPF ma obserwować procesy/hosta i wymaga uprawnień kernelowych. Przykładowy config znajduje się w `beyla/beyla-config.yml`.

Na labie można:
1. wyłączyć Java Agenta (`./scripts/disable-otel.sh`),
2. uruchomić Beylę na Linux VM,
3. wysłać jej OTLP do `http://localhost:4318`,
4. porównać widoczność z Java Agentem.

## 7. Reset środowiska

```bash
./scripts/reset-all.sh
```

Uwaga: usuwa również wolumeny z danymi Prometheusa, Loki, Tempo i PostgreSQL.

## Struktura

```text
apps/                  kod 3 usług Java
otel/                  Collector
prometheus/            Prometheus + rules
tempo/                 Tempo
loki/                  Loki
grafana/provisioning/  datasources/dashboard provisioning
alertmanager/           Alertmanager
load/                   k6
scripts/                operacje i scenariusze
beyla/                  opcjonalny lab eBPF
```

Więcej: `ARCHITECTURE.md` i `LABS.md`.
