# Automation Framework — Issues Backlog

Known design issues and limitations to address in future iterations.
Identified during architecture review. Listed in priority order within each section.

---

## Issue 4 — Retry with Dirty Class-Level State

**Severity:** Medium

**Description:**
`RetryAnalyzer` retries a failed test method automatically (up to `retry.max` times).
When the test class uses `@BeforeClass` for shared, expensive setup (e.g. a company is created
once and shared across methods), `@BeforeClass` does **not** re-run on retry — only the failed
test method is retried.

If the test left the shared company in a modified state (e.g. an incident was created, a meeting
was joined, a setting was changed), the retry runs against dirty state. This can produce
cascading failures that look like test bugs but are actually state pollution from the first attempt.

**Example scenario:**
```
@BeforeClass → creates company
Test-1 → passes (incident created on company)
Test-2 → fails midway (incident list is now non-empty from Test-1)
Test-2 retries → still fails (same dirty state, BeforeClass not re-run)
```

**Workaround (today):**
- Set `retry.max=0` for test classes that use `@BeforeClass` with shared mutable state.
- Design tests in such classes to be order-independent and read-only against the shared company.

**Planned fix:**
- Introduce a `@NoRetry` marker annotation that `RetryAnalyzer` checks before deciding to retry.
- Or: implement a cleanup step in the retry path that resets known state before re-running.

---

## Issue 5 — DataProvider + @TestSetup(createCompany=true) Creates N Companies

**Severity:** Medium

**Description:**
When a test uses TestNG's `@DataProvider` (e.g. to run with multiple data rows from Excel or
an inline array), `SetupOrchestrator.beforeInvocation()` fires for **each invocation** — one
per data row. If `@TestSetup(createCompany=true)` is set, one company is created and deleted
for each data row.

This may be intentional (isolated company per data set) or unintentional (one company should
serve all data rows). The current design has no way to express the difference.

**Example scenario:**
```java
@DataProvider
public Object[][] userRoles() {
    return new Object[][]{ {"admin"}, {"member"}, {"viewer"} };
}

@TestSetup(createCompany = true)
@Test(dataProvider = "userRoles")
public void shouldShowCorrectPermissions(String role) { ... }
// → 3 companies created and deleted. May be wrong.
```

**Workaround (today):**
- If one company is needed for all data rows: create it in `@BeforeClass` (class-level field)
  and use it in all data-driven iterations.
- If an isolated company per row is needed: the current behavior is correct — no workaround needed.

**Planned fix:**
- Add a `companyScope` field to `@TestSetup`: `Scope.METHOD` (default, current) vs `Scope.CLASS`.
- `CreateCompanyAction` checks `companyScope` and either uses `TestContextHolder` (method scope)
  or a `ClassContextHolder` (class scope).

---

## Issue 6 — Parallel Tests Mutating Shared Company Server-Side

**Severity:** Medium

**Description:**
When `parallel="methods"` is used and multiple test methods in the same class share a company
(created in `@BeforeClass`), the Java class field is read-only from all threads — that is safe.

However, the **actual company on the server** is not. If two tests call the API simultaneously
and both modify company configuration (enable/disable a feature, change a setting, create data),
they race against each other. The result is flaky tests that pass in isolation but fail in parallel.

**Example scenario:**
```
Test-1 in Thread-2 → enables deepfake alert for sharedCompany.getId()
Test-2 in Thread-3 → asserts deepfake alert is OFF for sharedCompany.getId()
→ race condition: result depends on which thread wins
```

**Workaround (today):**
- Use `@Test(singleThreaded = true)` on the test class to force sequential execution within it.
- Design tests that share a company to be purely read-only against that company's configuration.

**Planned fix:**
- Introduce a convention: `@BeforeClass` companies are read-only. Any test that modifies
  state uses `@TestSetup(createCompany=true)` to get its own isolated company per method.
- Document this convention in a CONTRIBUTING.md.

---

## Issue 7 — Two Company Sources in the Same Test Class Cause Naming Ambiguity

**Severity:** Low–Medium

**Description:**
A test class that has both:
- A `@BeforeClass` company (`private Company sharedCompany`)
- `@TestSetup(createCompany=true)` on some methods (→ `TestContextHolder.get().getCompany()`)

...now has two different companies accessible in different ways. A developer can easily read
from the wrong source. There is no compile-time guard against this.

**Example:**
```java
public class MixedScopeTest extends DeepfakeBaseTest {
    private CompanyDto sharedCompany;  // created in @BeforeClass

    @TestSetup(createCompany = true)
    @Test
    public void doSomething() {
        // Which company?
        CompanyDto c1 = sharedCompany;                               // class-level
        CompanyDto c2 = (CompanyDto) TestContextHolder.get().getCompany();  // method-level
        // Both compile. Both are different objects. Easy to use wrong one.
    }
}
```

**Workaround (today):**
- Convention: a test class either uses class-level OR method-level company, never both.
- Code review / naming: use descriptive names (`sharedCompany` vs `testCompany`) and
  document the pattern in comments.

**Planned fix:**
- Document the two-scope convention clearly in CONTRIBUTING.md.
- Consider a `ClassContextHolder` that is populated by `@BeforeClass` and automatically
  merged into `TestContext` by the orchestrator — unifying the access path.

---

## Issue 8 — TestContext.getDriver() Has a Side Effect on an Otherwise-Immutable Object

**Severity:** Low

**Description:**
`TestContext` is designed to be effectively immutable after `build()` returns — all fields
are `final`. However, `getDriver()` creates the WebDriver on first call (lazy initialization)
via an `AtomicReference.compareAndSet`. This is an intentional side effect: the browser is
opened at the moment of first Page Object construction, not at test start.

The side effect is thread-safe (via `AtomicReference`) and controlled, but it breaks the
strict immutability contract. If code assumes `TestContext` is truly immutable (e.g. for
caching, serialization, or equality checks), the lazy driver field will cause unexpected behavior.

**Current mitigation:**
- `AtomicReference.compareAndSet` ensures thread-safety for concurrent calls.
- A javadoc comment in `TestContext` explicitly documents this as an intentional exception
  to immutability.

**Planned fix (if needed):**
- Separate the driver reference into a `DriverHolder` companion object that is explicitly
  mutable, while `TestContext` carries only the configuration (browser type, headless flag).
- `BasePage` and teardown code interact with `DriverHolder` directly.
- `TestContext` becomes strictly immutable.
- Implement when/if strict immutability becomes a requirement (e.g. parallel state snapshots).

---

## Deferred Issues (Spring + Listeners)

### Issue 2 — Spring Beans Cannot Be Injected Into TestNG Listeners via @Listeners

**Severity:** High (deferred)

`@Listeners({SetupOrchestrator.class})` causes TestNG to instantiate listeners via their
no-arg constructor — Spring is not involved. Any `@Autowired` field in a listener will be null.

**Current workaround:** `SetupActionRegistry.register()` is called from `@BeforeSuite` methods
in base test classes, where Spring context IS available. Actions receive beans via constructor
injection at registration time, not via Spring's DI directly into the listener.

**Planned fix:** Implement a `SpringAwareListener` adapter that retrieves beans from the
Spring `ApplicationContext` (stored as a static reference) — or migrate to a
`ITestNGListener` factory pattern once Spring + TestNG integration matures.

---

### Issue 3 — CookieAuthProvider Is Not Thread-Safe for Parallel Runs

**Severity:** High (deferred)

`CookieAuthProvider` stores `capturedCookie` as a plain instance field. `DemoToolClient` is
a Spring singleton — one instance shared across all test threads. In `parallel="methods"`,
multiple threads call `authenticate()` simultaneously, causing race conditions on the cookie field.

**Current workaround:** Tests are currently single-threaded (no `parallel` in `deepfake-suite.xml`).

**Planned fix:** Synchronize `authenticate()` or store the cookie in a `ThreadLocal` with
a session-expiry check. Alternatively, scope `DemoToolClient` as `prototype` in Spring
so each test gets its own instance.
