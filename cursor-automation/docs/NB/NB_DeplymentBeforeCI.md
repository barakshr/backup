# NB CI Testing

## Jenkins PipeLine

1. Build NB  image 
2. Deploy NB docker instance with `docker compose` 
3. Set NB env so Graph base URL hits host WireMock 
4. Run`mvn test` (tests start WireMock, register stubs, call NB, verify).
5. Stop NB (`docker compose down`).

Example shape (illustrative only):

```yaml
services:
  notification-bot:
    image: notification-bot:${BUILD_TAG:-latest}
    ports:
      - "8080:8080"
    environment:
      GRAPH_API_BASE_URL: http://host.docker.internal:8089
    extra_hosts:
      - "host.docker.internal:host-gateway"
```

Adjust env var names and ports to match the real NB application.

---



---

## Quick checklist

- [ ] NB outbound “Graph” URL points to WireMock reachable from the container.
- [ ] Automation NB base URL points to NB reachable from the test process.
- [ ] WireMock port is agreed between NB env and tests (fixed port in CI is simplest).
- [ ] Staging tests use a separate config profile without WireMock assumptions.
