# spring-observability2

A Spring Boot 4.1 service instrumented with OpenTelemetry, wired to a local
metrics/traces/logs stack running under Docker Compose.

## Running

```bash
./mvnw spring-boot:run
```

Spring Boot starts the Compose stack automatically and leaves it running when the
app stops. Tear it down explicitly when you're done:

```bash
docker compose down
```

## Endpoints

| What                | URL                                      |
|---------------------|------------------------------------------|
| Sample endpoint     | http://localhost:8080/hello              |
| Health              | http://localhost:8080/actuator/health    |
| Grafana             | http://localhost:3000 (no login)         |
| Prometheus          | http://localhost:9090                    |
| Tempo               | http://localhost:3200                    |
| Loki                | http://localhost:3100                    |

`/hello` makes an outbound call to httpbin.org, so a request produces a server
span plus a nested client span.

## How telemetry flows

The app exports all three signals over OTLP/HTTP to the collector on port 4318.
The collector fans them out: traces to Tempo, metrics to a Prometheus exporter
that Prometheus scrapes, and logs to Loki.

```
app --OTLP/HTTP:4318--> otel-collector --+--> Tempo       (traces)
                                         +--> Prometheus  (metrics, scraped :8889)
                                         +--> Loki        (logs)
```

Grafana is provisioned with all three datasources. Log lines carry the `traceid`
of the request that emitted them, so you can jump from a log in Loki straight to
its trace in Tempo and back.

## Notes

Compose images are pinned. `grafana/tempo:latest` in particular must not be used:
Tempo 2.10 moved the ingester to a live-store that the querier ring does not see,
which makes every trace query fail with `empty ring`.
