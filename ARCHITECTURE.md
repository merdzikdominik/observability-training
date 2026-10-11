# Architektura laboratorium

```text
                               Linux VM uczestnika

   +------------------------- Docker Compose --------------------------+
   |                                                                  |
   |  k6 ---> orders-api ---> inventory-api                           |
   |            |   \             (8081)                              |
   |            |    \--> payment-api                                 |
   |            |          (8082)                                     |
   |            +----> PostgreSQL                                     |
   |                                                                  |
   |  Java Agent --OTLP--> OTel Collector --traces--> Tempo           |
   |                           |              \                        |
   |                           |               \-> Prometheus metrics  |
   |                           +--metrics--> Prometheus                 |
   |                           +--logs----> Loki                        |
   |                                                                  |
   |                 Grafana <--- Prometheus / Tempo / Loki            |
   |                                                                  |
   |                 Prometheus ---> Alertmanager ---> webhook         |
   +------------------------------------------------------------------+

   Optional host lab: Beyla / OBI (eBPF) ---> OTel Collector :4318
```

## Dlaczego Compose

Compose jest warstwą uruchomieniową, a nie tematem szkolenia. Wszystkie konfiguracje są zwykłymi plikami w repozytorium i są montowane do kontenerów jako bind mounts. Uczestnik edytuje YAML na VM i restartuje tylko odpowiedni komponent.

Przykład:

```bash
vim otel/config.yaml
docker compose restart otel-collector
docker compose logs -f otel-collector
```
