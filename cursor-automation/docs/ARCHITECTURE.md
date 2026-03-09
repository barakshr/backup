# IS Automation Framework — Architecture & Design Document

> **Purpose:** Living document for design and architecture decisions.
> Updated as the project evolves. Open this file in a new chat to continue from where we left off.

---

## 1. Project Context

### What is IronScales?
IronScales (IS) is an email security company. Their product is a multi-tenant SaaS platform.
This automation framework covers testing of their products — starting with the **Deepfake Detection System (DFS)**.

### What is the DFS?
The Deepfake Detection System monitors Microsoft Teams meetings for AI-generated (deepfake) video of participants.

**How it works (user perspective):**
- Only works with Microsoft Teams (not Zoom, etc.)
- When a Teams meeting has an **external participant** (someone not on the company's tenant), the system monitors their video
- If the video is detected as AI-generated (deepfake), it notifies the meeting organizer and internal participants
- Notifications appear as a Teams popup and as a record in the IronScales dashboard

**Terminology:**
- **Internal participant** — uses their company's Teams account (same tenant as the organization)
- **External participant** — joins from an external account (e.g. their personal Gmail/Teams account)
- **Identity Protection List** — list of company employees whose faces are registered for comparison. If an external user's face matches a registered employee, the system checks for deepfake.
- **Meeting organizer** — person who scheduled the meeting in calendar. Receives deepfake alerts.

**Detection scenario:**
1. Internal employee (A) schedules a Teams meeting
2. Internal employee (B) joins
3. External participant (C) joins — could be a deepfake impersonating someone in the Identity Protection List
4. Recording Bot detects the external participant and captures screenshots
5. Call Server analyzes screenshots against the Identity Protection List using AI models
6. If suspicious: Notification Bot joins the call and displays a popup to all internal participants

---

## 2. DFS System Architecture

### Services (all run in AWS EKS — DeepFake namespace)

| Service | Role |
|---|---|
| **Recording Bot** (Windows pod) | Joins every Teams call. Detects external participants. Captures screenshots at intervals. Sends to Call Server. Supports up to 9 external participants per call. |
| **Call Server** | Brain of the system. Receives metadata + image streams from RB. Uses AI models (internal DFTraces model + AWS Rekognition as fallback) to score each frame. Decides if deepfake. Notifies Notification Bot directly (new flow). Writes results to DB. |
| **Notification Bot** (Windows pod) | Receives deepfake alert from Call Server. Joins the Teams meeting. Sends popup to all internal participants via Microsoft Graph API. |
| **Data Proxy** | Bridge between DFS services and the IronScales Monolith. Handles configuration from the monolith, stores it in DB. Pass-through for most operations. |
| **AI-Trace Model** | Internal AI model pod for image comparison. Used before falling back to AWS Rekognition. |
| **Demo Bot** | Internal tool pod. Used to join Teams meetings with pre-generated deepfake videos. Primarily for QA and sales demos. |

### External dependencies

| Dependency | Role |
|---|---|
| **Microsoft Teams** | The communication platform being monitored |
| **Microsoft Graph API** | Used by Recording Bot and Notification Bot to interact with Teams |
| **AWS Rekognition** | External AI model for deepfake scoring (fallback after internal model) |
| **Amazon ElastiCache (Redis)** | Caching layer |
| **Postgres DB + PGVector** | Stores call data, participant data, company config, analytics, identity media |
| **Images S3 Bucket** | Stores key person photos (Identity Protection List) |
| **Videos S3 Bucket** | Stores demo tool videos |

### Notification flow (current)
```
Call Server → Notification Bot → Microsoft Graph API → Teams popup
```
(Previous flow was CS → Recording Bot → NB; this has been updated to direct CS → NB)

### IS Monolith (separate EKS namespace)
- External service from DFS's perspective
- Handles the IronScales customer-facing UI and REST API
- Data Proxy is the DFS channel to the monolith
- Company creation, tenant management, feature flags go through the monolith

---

## 3. DB Schema (key tables)

| Table | Purpose |
|---|---|
| `companies` | tenant_id, name, showPopup, endCall, useAiTracesModel |
| `calls` | call metadata: tenant_id, company_id, call_chain_id, timestamps |
| `call_participant` | per-participant data: person_id, external flag, tenant_id |
| `call_logs` | organizer info, participants JSON, start/end time, call_type |
| `people` | Identity Protection List entries: office365_user_id, profile_id, company_id, tenant_id |
| `person_media` | Photos for each person: s3_key, media_type, embedding, is_deleted |
| `deepfake_person` | Detection results: call_id, person_id, confidence, detected_at, confirmed_at |
| `deepfake_person_media` | Per-frame data: s3_key, fake probability, rekognition_similarity |
| `analytics` | Aggregated stats: calls_count, deepfake_count, key_people_count per hour |
| `whitelist` | Allowlist entries per company |

---

## 4. Customer Onboarding (how IS gets integrated into a customer's Azure/Teams)

Two applications need to be granted admin consent in the customer's O365 tenant:

1. **Media Application (Recording Bot)** — joins calls silently, accesses audio/video
   - Consent via admin link: `https://login.microsoftonline.com/common/adminconsent?client_id={media_app_id}`
   - Teams compliance recording policy applied via PowerShell script

2. **Notification Application (Notification Bot)** — sends in-call messages
   - App manifest uploaded to Teams Admin Center
   - Setup policies configured so the app auto-joins relevant meetings

3. **Demo Tool** (for testing only)
   - Separate consent for testing purposes

**Consent environments:**

| Environment | App ID |
|---|---|
| Production EU | `0808459f-3856-496e-8b73-4c4db5b92767` |
| Production US | `827b8f30-e8e0-47f9-8176-2294bd27ef95` |
| QA | `f4a39aeb-a5f6-4784-8aae-695019d7b7e6` |
| Staging | `87acd09a-c972-414b-a27e-769193401bd8` |

---

## 5. IS Product UI (customer-facing)

After onboarding, the IS admin enables DFS for a company via the IS admin panel.
This sends tenant_id + company_id to Data Proxy → DB.

The customer then configures via the IS UI (Deepfake tab):

1. **Identity Protection List** — add employees + upload their photos (up to 10 per person). Profile status: Incomplete / Complete.
2. **Settings** — popup alerts, email alerts, consent management, live deepfake alert toggle.
3. **Meetings Overview** — view all Teams calls, filter by status (Safe / Suspicious), see per-meeting stats (internals, externals, VIPs, status).

---

## 6. Demo Tool

Internal tool for QA and sales demos. Available at:
`https://deepfake-demo-server.staging.ironscales.io`

**Purpose:** Join a Teams meeting with a pre-recorded deepfake video, triggering the DFS detection flow.

**Key API endpoints:**

| Method | Path | Description |
|---|---|---|
| POST | `/auth/login` | Login (form-urlencoded), returns JWT in Set-Cookie on 303 redirect |
| GET | `/calls/status` | Current call status |
| GET | `/dashboard/calls` | List all calls |
| POST | `/calls/join-multiple` | Join a Teams meeting as a deepfake participant |
| POST | `/calls/leave-call` | Leave an active call |
| POST | `/calls/trigger-popup` | Manually trigger the deepfake popup in the meeting |
| GET | `/videos/videos` | List available deepfake videos |
| POST | `/videos/create` | Create a new deepfake video from an image |

**Auth flow:** POST `/auth/login` with `application/x-www-form-urlencoded` body → server returns 303 redirect with `Set-Cookie` containing the JWT → use that cookie on all subsequent requests.

**Join call request body (`POST /calls/join-multiple`):**
```json
{
  "video_id": "string",
  "meeting_link": "https://teams.microsoft.com/...",
  "display_names": ["Display Name"],
  "tenant_id": "uuid",
  "organizer_user_id": "uuid",
  "call_stay_time_minutes": 5
}
```

---

## 7. Automation Framework Design

### Philosophy
- Start small, **add** capabilities — never restructure
- Standard OOP and automation terminology (Page Object, client, DTO, base class) — no internal IS jargon (no "scenarios", "stories")
- Infra has zero IronScales product knowledge — reusable by any team
- Every structural decision was made so it does not need to change, only extend

### Key design decisions

| Decision | Choice | Reason |
|---|---|---|
| Build tool | Maven multi-module | Enforces dependency direction at compile time; teams own their modules |
| HTTP client | RestAssured | Industry standard for Java API testing; fluent API; native Allure integration |
| Auth abstraction | `AuthProvider` interface | DemoTool uses cookies, future services may use Bearer — same client base |
| Config | Properties file + env var override | Dev uses file, CI/Jenkins uses env vars — no file changes for CI |
| Test framework | TestNG | Already used in existing IS project; suite XML for parallel support |
| Assertions | AssertJ | Fluent, readable, better error messages than JUnit assertions |
| Logging | SLF4J + Logback | Standard; configured in infra, inherited by all modules |
| Reporting | Allure (stub) | Wired in stage 2; filter added to RestAssured, listener wired to TestNG |

### Module dependency flow
```
automation-deepfake  →  automation-common  →  automation-infra
(future: automation-email-security, automation-simulation)
```
One direction only. Product modules cannot be imported by infra or common.

---

## 8. Project Structure

```
cursor-automation/                              # Parent POM
│
├── automation-infra/                           # Full technical toolkit — zero product knowledge
│   └── src/main/java/com/is/infra/
│       ├── http/
│       │   ├── BaseApiClient.java              # IMPLEMENTED — RestAssured wrapper
│       │   ├── AuthProvider.java               # IMPLEMENTED — interface
│       │   ├── CookieAuthProvider.java         # IMPLEMENTED — JWT from Set-Cookie (303 redirect)
│       │   ├── BearerTokenAuthProvider.java    # STUB
│       │   ├── ApiResponse.java                # IMPLEMENTED — typed Response wrapper
│       │   ├── ApiException.java               # IMPLEMENTED — rich error context
│       │   └── RetryPolicy.java                # STUB
│       ├── config/
│       │   └── ConfigManager.java              # IMPLEMENTED — properties + env var override
│       ├── utils/
│       │   └── Poller.java                     # STUB — eventual consistency polling
│       ├── reporting/
│       │   └── AllureHelper.java               # STUB — Allure attachments
│       ├── selenium/
│       │   ├── BasePage.java                   # STUB — Page Object base
│       │   ├── DriverFactory.java              # STUB
│       │   └── DriverOptions.java              # STUB
│       ├── testng/
│       │   ├── BaseTest.java                   # IMPLEMENTED — global lifecycle, config init
│       │   ├── BaseApiTest.java                # IMPLEMENTED — API test base (empty, hierarchy holder)
│       │   ├── BaseUiTest.java                 # STUB — driver lifecycle
│       │   ├── TestListener.java               # STUB — failure hooks, Allure lifecycle
│       │   └── RetryAnalyzer.java              # STUB
│       ├── database/
│       │   └── DatabaseClient.java             # STUB — JDBC DB assertions (stage 3)
│       ├── mock/
│       │   └── MockServerManager.java          # STUB — WireMock (stage 2)
│       └── parallel/
│           └── ResourcePool.java               # STUB — dev account pool (stage 3)
│   └── src/main/resources/
│       ├── config.properties                   # Default config (environment, browser)
│       └── logback.xml                         # Logging config
│
├── automation-common/                          # Shared IS business logic
│   └── src/main/java/com/is/common/
│       ├── clients/
│       │   ├── MembersApiClient.java           # STUB — IS monolith members API
│       │   └── AutomationApiClient.java        # STUB — company creation, feature flags
│       ├── dto/
│       │   └── CompanyDto.java                 # STUB
│       └── workflows/
│           └── CompanySetupWorkflow.java       # STUB — create + configure company
│
├── automation-deepfake/                        # Deepfake team module
│   ├── src/main/java/com/is/deepfake/
│   │   ├── clients/
│   │   │   ├── DemoToolClient.java             # IMPLEMENTED — login, getCallStatus, joinCall...
│   │   │   ├── CallServerClient.java           # STUB
│   │   │   └── NotificationBotClient.java      # STUB
│   │   ├── dto/
│   │   │   ├── DemoToolLoginResponse.java      # IMPLEMENTED
│   │   │   ├── DemoToolCallRequest.java        # IMPLEMENTED
│   │   │   └── DemoToolCallResponse.java       # IMPLEMENTED (stub fields)
│   │   └── config/
│   │       └── DeepfakeConfig.java             # IMPLEMENTED — DF-specific config keys
│   └── src/test/java/com/is/deepfake/tests/
│       ├── BaseDeepfakeApiTest.java            # IMPLEMENTED — inits DemoToolClient
│       └── api/
│           └── DemoToolLoginTest.java          # IMPLEMENTED — login + GET + assert (2 tests)
│   └── src/test/resources/
│       ├── config.properties                   # DF config: base URL, credentials placeholders
│       └── testng/deepfake-suite.xml           # DF TestNG suite
│
└── docs/
    └── ARCHITECTURE.md                         # This file
```

---

## 9. Completed Stages

### Stage 1 — Skeleton (DONE)
- [x] Multi-module Maven project compiles cleanly (Java 17, zero warnings)
- [x] Full package structure created — implemented classes + stubs
- [x] `DemoToolClient` — login + GET with cookie auth
- [x] `DemoToolLoginTest` — 2 tests: login + GET `/calls/status` and GET `/dashboard/calls`
- [x] `ConfigManager` — properties file + env var override
- [x] `CookieAuthProvider` — handles 303 redirect + Set-Cookie JWT capture
- [x] `AuthProvider` interface — `BearerTokenAuthProvider` stub for future services
- [x] All future capability namespaces reserved: `database/`, `mock/`, `parallel/`

---

## 10. Roadmap — Future Stages

### Stage 2 — Service Tests + Reporting
**Goal:** Test Notification Bot in isolation using WireMock

- Implement `MockServerManager` (WireMock)
- Implement `NotificationBotClient`
- NB service test: send trigger request → verify NB called Graph API mock with correct body
- Wire Allure: `TestListener` + `AllureHelper` + RestAssured filter
- Wire `RetryAnalyzer` for flaky test handling

### Stage 3 — Call Server Tests + DB + Parallel
**Goal:** Test Call Server with metadata and image input, assert DB state

- Implement `CallServerClient`
- Implement `DatabaseClient` (JDBC → Postgres)
- CS service tests: send participant metadata → assert DB record
- Implement `ResourcePool` for parallel test execution with dev account pool
- Add parallel TestNG configuration

### Stage 4 — Company Setup + Common Business Layer
**Goal:** Enable end-to-end tests that require a real IS company

- Implement `AutomationApiClient` (company create/delete, feature flags)
- Implement `MembersApiClient` (feature flag management for DFS)
- Implement `CompanySetupWorkflow`
- DF end-to-end tests with real company: enable DFS → add protected identity → assert

### Stage 5 — UI Tests
**Goal:** End-to-end tests through the IS browser UI

- Implement `DriverFactory`, `DriverOptions`, `BasePage`
- Implement `BaseUiTest` (driver lifecycle)
- DF UI page objects: IdentityProtectionListPage, MeetingsOverviewPage
- E2E test: login → add key person → trigger deepfake → assert dashboard metrics

### Stage 6 — Jenkins CI Integration
- Jenkinsfile at project root
- Nightly build configuration (run deepfake suite on schedule)
- Dynamic environment support (branch-based namespace in k8s staging cluster)
- Allure report publishing

---

## 11. Running the Tests

### Prerequisites
Set credentials as env vars or in `automation-deepfake/src/test/resources/config.properties`:

```bash
export DEMO_TOOL_USERNAME=your_username
export DEMO_TOOL_PASSWORD=your_password
```

Or edit the file directly (do not commit credentials):
```properties
demo.tool.username=your_username
demo.tool.password=your_password
```

### Run commands

```bash
# Compile all modules
mvn clean compile

# Run Deepfake tests only
mvn test -pl automation-deepfake

# Run specific test class
mvn test -pl automation-deepfake -Dtest=DemoToolLoginTest

# Run with a custom suite XML
mvn test -pl automation-deepfake -DsuiteXmlFile=src/test/resources/testng/deepfake-suite.xml
```

### Environment (Stage 1 target)
- DemoTool: `https://deepfake-demo-server.staging.ironscales.io`
- IS environment: QA (`https://members.qa.ironscales.io`)

---

## 12. Key Design Principles (reference for new contributors)

1. **Infra is generic** — `automation-infra` has no IronScales product knowledge. A developer from any company could use it.
2. **Clients are thin** — `DemoToolClient` calls endpoints and returns `ApiResponse`. No assertions, no business logic inside clients.
3. **DTOs are POJOs** — explicit contracts. Use `@JsonIgnoreProperties(ignoreUnknown = true)` to tolerate API changes.
4. **Config in one place** — `ConfigManager.getRequired("key")` throws clearly if a key is missing. No `System.getenv()` scattered in test code.
5. **Stubs document intent** — stub classes explain what they will do and why. They are not empty files.
6. **Add, don't change** — the structure is designed so new capabilities slot in as new classes or filled-in stubs. No restructuring should be needed.
7. **Standard terminology** — Page Object, client, DTO, base class, workflow. Not scenario, story, accessor, or other internal IS automation terms.

---

## 13. Reference — Existing IS Automation Project

The legacy IS automation project (`ironscales-qa-automation-framework`) was scanned and used as inspiration.
It is a single-module project with ~2,900 Java files.

**Key things we adopted:**
- OkHttp singleton pattern → we use RestAssured (simpler for testing)
- DemoToolApiClient pattern (form login, cookie capture) → we cleaned it up in `CookieAuthProvider`
- `@TestInfo` annotation pattern for declarative setup → considered for future stage
- Thread-local company factory → considered for `ResourcePool` in stage 3
- Awaitility for polling → we built our own simpler `Poller`

**Key things we did differently:**
- Multi-module instead of single module
- Standard terminology (no scenario/story layers)
- `ConfigManager` with env var override instead of `GeneralConfigurationEnums` enum
- No `API_LIST.json` JSON-driven URL mapping — clients own their endpoint constants
- No `ProductAPI` singleton routing through a single factory — each client is independent
