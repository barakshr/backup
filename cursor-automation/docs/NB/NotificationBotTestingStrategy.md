
## Test Categories

### Category A: Happy Path

| # | Test Name | Description | Steps | Verification |
|---|-----------|-------------|-------|-------------|
| A1 | `triggerAlertWithValidPayload` | Send a valid deepfake alert trigger to NB | 1. Stub Graph API mock to return 201 on POST `/v1.0/chats/{chatId}/messages` 2. POST valid trigger payload to NB `/api/v1/notifications/trigger` | - NB returns HTTP 200 - Response body contains `status: "SENT"` - WireMock verify: Graph API received exactly 1 POST with correct message body |
| A2 | `triggerAlertMultipleParticipants` | Trigger alert when meeting has multiple internal participants | 1. Stub Graph API mock to return 201 2. POST trigger with 3 internal participants | - NB returns 200 - `recipientCount` equals 3 - WireMock verify: Graph API received 3 POST calls (one per participant) |
| A3 | `triggerAlertDifferentThreatLevels` | Verify that threat level is reflected in the Teams message | 1. Stub Graph API mock 2. POST trigger with `threatLevel: "MEDIUM"` | - NB returns 200 - WireMock verify: Graph API message body contains "MEDIUM" threat text |

### Category B: Error Handling (Graph API Failures)

| # | Test Name | Description | Steps | Verification |
|---|-----------|-------------|-------|-------------|
| B1 | `graphApiReturns401_nbReturnsError` | Graph API rejects the request (expired token) | 1. Stub Graph API mock to return 401 on POST 2. POST valid trigger to NB | - NB returns HTTP 502 (Bad Gateway) - Response body contains error detail about Graph API auth failure - WireMock verify: Graph API was called exactly 1 time |
| B2 | `graphApiReturns500_nbReturnsError` | Graph API is down | 1. Stub Graph API mock to return 500 2. POST valid trigger to NB | - NB returns HTTP 502 - Response body contains error detail about downstream failure |
| B3 | `graphApiTimeout_nbReturnsTimeout` | Graph API does not respond in time | 1. Stub Graph API mock with a fixed delay (e.g. 30s) 2. POST valid trigger to NB | - NB returns HTTP 504 (Gateway Timeout) - Response returns within NB's own timeout threshold |

### Category C: Input Validation

| # | Test Name | Description | Steps | Verification |
|---|-----------|-------------|-------|-------------|
| C1 | `missingCallId_returns400` | Required field `callId` is missing | 1. POST trigger payload without `callId` | - NB returns HTTP 400 - Error message mentions `callId` |
| C2 | `emptyParticipantsList_returns400` | No one to notify | 1. POST trigger with empty `internalParticipants` array | - NB returns HTTP 400 - Error message mentions participants |
| C3 | `invalidTenantIdFormat_returns400` | `tenantId` is not a valid UUID | 1. POST trigger with `tenantId: "not-a-uuid"` | - NB returns HTTP 400 |

### Category D: Edge Cases

| # | Test Name | Description | Steps | Verification |
|---|-----------|-------------|-------|-------------|
| D1 | `largeParticipantList_handledCorrectly` | Meeting with 20+ internal participants | 1. Stub Graph API mock 2. POST trigger with 25 participants | - NB returns 200 - WireMock verify: 25 POST calls to Graph API |
| D2 | `healthCheck_returnsUp` | Basic liveness check | 1. GET `/health` | - Returns 200 - Body contains `{ "status": "UP" }` |

---

## Priority for This Practice Round

**Focus: A1 and B1 only.**

These two tests cover the core pattern:
- A1 proves the happy path works and teaches the WireMock stub + verify pattern
- B1 proves error propagation and teaches WireMock error simulation

All other tests follow the same pattern and can be added incrementally.
