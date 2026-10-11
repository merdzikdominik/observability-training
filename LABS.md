# Pomysły na zadania

## OTel / Collector
1. Uruchom aplikacje bez Java Agenta i potwierdź brak trace'ów aplikacyjnych.
2. Włącz `compose.otel.yaml` i znajdź pierwszy trace `POST /api/orders`.
3. Dodaj do `resource/training` atrybut `team=training` i sprawdź go w Tempo.
4. Dodaj `debug` exporter do pipeline trace i obserwuj dane w logach Collectora.
5. Zbuduj prosty `filter` processor usuwający wybrany endpoint techniczny.
6. W logach znajdź `customer_email` i przygotuj redaction w Collectorze.

## Prometheus / PromQL
1. Znajdź metryki HTTP wygenerowane przez Java Agenta.
2. Policz request rate per `service_name`.
3. Policz error rate per serwis.
4. Znajdź histogram czasu odpowiedzi i wyjaśnij `_bucket`, `_sum`, `_count`.
5. Policz p50/p95/p99.
6. Porównaj `rate(...[30s])` i `rate(...[5m])`.
7. Znajdź top 3 najwolniejsze trasy.
8. Zbuduj recording rule dla request rate.
9. Otwórz `training_requests_total` z `cardinality-demo`, policz liczbę serii i wskaż label powodujący eksplozję kardynalności.
10. Przygotuj `metric_relabel_configs`, które usuwa `user_id` albo całą demonstracyjną metrykę.

## Loki / LogQL
1. Wyszukaj `{service_name="payment-api"}`.
2. Użyj `| logfmt` i odfiltruj `level="ERROR"`.
3. Policz `count_over_time` błędów per serwis.
4. Znajdź log po `trace_id` i przejdź do Tempo.
5. Znajdź najbardziej "gadatliwy" serwis.

## Incydenty
- `./scripts/scenario-latency.sh`: znajdź źródło wzrostu p95.
- `./scripts/scenario-errors.sh`: znajdź źródło 5xx.
- `./scripts/scenario-mixed.sh`: znajdź dwa niezależne problemy i pokaż dowody w metrics + traces + logs.
