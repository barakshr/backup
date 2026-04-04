# NB CI Testing

## Jenkins + Docker Compose (NB only)

1. **Build** NB image (tag with build id).
2. **Start** NB with `docker compose` (single service is enough).
3. **Set** NB env so Graph base URL hits host WireMock (`host.docker.internal` on Docker Desktop; on Linux CI use `extra_hosts` or host gateway IP as your platform documents).
4. **Run** `mvn test` (tests start WireMock, register stubs, call NB, verify).
5. **Stop** NB (`docker compose down`).

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

## Staging / dynamic environment (no WireMock)

For **integration** tests against a real deployed NB:

- `notification.bot.base.url` = real ingress URL (staging, branch namespace, etc.).
- NB uses **real** Microsoft Graph (or your org’s test tenant).
- Do **not** rely on `mockServer.verify` for Graph; assert on HTTP responses, DB, or downstream observability instead.

---

## Quick checklist

- [ ] NB outbound “Graph” URL points to WireMock reachable from the container.
- [ ] Automation NB base URL points to NB reachable from the test process.
- [ ] WireMock port is agreed between NB env and tests (fixed port in CI is simplest).
- [ ] Linux CI: `host.docker.internal` works (e.g. `extra_hosts: host-gateway`).
- [ ] Staging tests use a separate config profile without WireMock assumptions.
