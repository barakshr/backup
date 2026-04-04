# Call Server -- Testing Strategy





**Phase 1 flow (no WireMock):**
```
TestNG (simulates RB)  --POST-->  Call Server  -->  Postgres DB  --> JDBC --  TestNG (assert)
```

**Phase 2 flow (with WireMock):**
```
For simulating deepfake score from AWS
TestNG (simulates RB)  --POST-->  Call Server ->AWS Mock-> CS  -->  Postgres DB  --> JDBC--  TestNG (assert)

|
TestNG (simulates RB)  --POST-->  Call Server -> Notifcation Bot Mock-->    TestNG (assert)

```



---

## Test Categories

### Phase 1: Metadata Tests (No WireMock -- DB assertions only)

These verify that the CS correctly handles call lifecycle events and writes to the database.
The test acts as the Recording Bot, sending metadata via HTTP and asserting DB state via JDBC.

| # | Test Name | Description | Input | Verification |
|---|-----------|-------------|-------|-------------|
| M1 | `newCallTracked_appearsInDb` | RB notifies CS of a new call | POST `/calls/track` with valid metadata | DB: row in `calls` table with correct `call_id`, `tenant_id`. CS returns 200 with `status: "TRACKED"` |
| M2 | `internalParticipantJoined_notMonitored` | Internal user joins the call | POST `/calls/{callId}/participant-joined` with `external: false` | CS returns `decision: "SKIP"`. DB: `call_participant` row with `external=false` |
| M3 | `externalParticipantJoined_monitoringStarted` | External user joins the call | POST `/calls/{callId}/participant-joined` with `external: true` | CS returns `decision: "MONITOR"`. DB: `call_participant` row with `external=true` |
| M4 | `invalidTenantId_returns400` | Bad tenant in track request | POST `/calls/track` with invalid `tenantId` | CS returns HTTP 400 |

### Phase 2: Detection Tests (WireMock + DB assertions)

These test the core deepfake detection logic. The test sends screenshots and uses WireMock
to simulate AI model responses. Verification combines WireMock verify (did the CS call the
right services?) with DB assertions (did the CS write the correct verdict?).

| # | Test Name | Description | WireMock Stubs | Verification |
|---|-----------|-------------|----------------|-------------|
| D1 | `screenshotAnalyzed_noDeepfake` | AI model says "real" | AI-Trace returns low fake score | DB: no `deepfake_person` row (or confidence below threshold). WireMock: AI-Trace called 1x |
| D2 | `screenshotAnalyzed_deepfakeDetected` | AI model says "fake" | AI-Trace returns high fake score. NB mock accepts trigger | DB: `deepfake_person` row with high confidence. WireMock: AI-Trace called 1x, NB trigger called 1x |
| D3 | `aiTraceInconclusive_fallbackToRekognition` | AI-Trace borderline, Rekognition confirms fake | AI-Trace returns borderline score. Rekognition returns fake | DB: `deepfake_person` with `detected_by_dftracesmodel=false`. WireMock: both AI-Trace AND Rekognition called |
| D4 | `aiTraceDown_rekognitionFallback` | AI-Trace is down, Rekognition used as fallback | AI-Trace returns 500. Rekognition returns result | WireMock: Rekognition called. CS does not crash |
| D5 | `deepfakeDetected_nbTriggeredCorrectly` | Verify the NB trigger request content | AI-Trace returns fake. NB mock accepts | WireMock: NB received POST with correct `callId`, `participantId`, `confidence` in body |

### Phase 3: Error Handling and Edge Cases

| # | Test Name | Description | Verification |
|---|-----------|-------------|-------------|
| E1 | `bothAiModelsTimeout_csHandlesGracefully` | AI-Trace and Rekognition both time out | CS returns error but does not crash |
| E2 | `duplicateCallTrack_idempotent` | POST `/calls/track` twice with same callId | Second call returns 409 or is silently ignored |
| E3 | `screenshotForUnknownCall_returns404` | Screenshot for a callId never tracked | CS returns 404 |
| E4 | `healthCheck_returnsUp` | Basic liveness | GET `/health` returns 200 with `status: "UP"` |

---

## Priority for First Round

**Focus: M1, M3, D2.**

These three tests cover the core patterns needed for all future CS tests:

- **M1** proves the `CallServerClient` + `DatabaseClient` (JDBC) assertion pattern -- simplest possible test
- **M3** proves the participant decision flow and extends M1 with response body assertions
- **D2** introduces the full detection chain: screenshot input --> AI model mock --> DB write --> NB trigger verify

All other tests follow the same patterns and can be added incrementally.

---

## Infrastructure Required

| Component | Location | Status |
|-----------|----------|--------|
| `CallServerClient` | `automation-deepfake/.../clients/` | Stub exists, needs implementation |
| CS DTOs (request/response) | `automation-deepfake/.../dto/` | New |
| `DatabaseClient` (JDBC) | `automation-infra/.../database/` | Stub exists, needs implementation |
| `AiTraceModelStubs` | `automation-deepfake/.../mock/` | New |
| `RekognitionStubs` | `automation-deepfake/.../mock/` | New |
| `NotificationBotStubs` | `automation-deepfake/.../mock/` | New |
| Config: `call.server.base.url`, DB properties | `DeepFakeConfig` + `config.properties` | Partial (commented out) |
| `CallServerServiceTest` | `automation-deepfake/.../tests/api/` | New |
