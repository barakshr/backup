package com.is.deepfake.tests.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.is.deepfake.clients.CallServerClient;
import com.is.deepfake.config.DeepFakeConfig;
import com.is.deepfake.dto.CallTrackRequest;
import com.is.deepfake.dto.CallTrackResponse;
import com.is.deepfake.testng.ServiceBaseTest;
import com.is.infra.database.DatabaseClient;
import com.is.infra.http.ApiResponse;

/**
 * Service-level tests for the Call Server.
 * <p>
 * Phase 1 (metadata tests): TestNG simulates the Recording Bot by sending
 * call metadata to the CS, then asserts that the correct rows appear in the
 * Postgres DB via JDBC. No WireMock needed — the CS only writes to the DB.
 * <p>
 * Phase 2 (detection tests — future): will add WireMock stubs for AI-Trace
 * Model, AWS Rekognition, and the Notification Bot.
 * <p>
 * Extends {@link ServiceBaseTest} so WireMock is available for Phase 2 tests
 * without changing the class hierarchy.
 */
public class CallServerServiceTest extends ServiceBaseTest {

    private CallServerClient csClient;
    private DatabaseClient db;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "startMockServer")
    public void setupCallServerClient() {
        DeepFakeConfig cfg = DeepFakeConfig.get();
        csClient = new CallServerClient(cfg.getCallServerBaseUrl());
        db = new DatabaseClient(
                cfg.getDbHost(), cfg.getDbPort(), cfg.getDbName(),
                cfg.getDbUser(), cfg.getDbPassword());
    }

    @AfterClass(alwaysRun = true)
    public void closeDatabaseConnection() {
        if (db != null) {
            db.close();
        }
    }

    // ─── M1: New call tracked → row appears in DB ───

    /*
     * Flow:
     * 1. Generate a unique callId (UUID) so this test is parallel-safe
     * 2. Build a CallTrackRequest (simulating what the Recording Bot sends)
     * 3. POST it to the Call Server's /calls/track endpoint
     * 4. Assert the CS returns 200 with status "TRACKED"
     * 5. Query the Postgres DB via JDBC and assert a row exists in the
     *    `calls` table with the matching call_id and tenant_id
     */

    @Test(description = "CS: track a new call -> row appears in calls table")
    public void newCallTracked_appearsInDb() {
        String uniqueCallId = UUID.randomUUID().toString();
        String tenantId = "tenant-" + UUID.randomUUID();

        CallTrackRequest request = CallTrackRequest.builder()
                .callId(uniqueCallId)
                .tenantId(tenantId)
                .organizerId("organizer-" + UUID.randomUUID())
                .meetingLink("https://teams.microsoft.com/l/meetup-join/test-meeting-" + uniqueCallId)
                .build();

        ApiResponse response = csClient.trackCall(request);

        assertThat(response.getStatusCode()).isEqualTo(200);
        CallTrackResponse body = response.as(CallTrackResponse.class);
        assertThat(body.getStatus()).isEqualTo("TRACKED");
        assertThat(body.getCallId()).isEqualTo(uniqueCallId);

        Map<String, Object> dbRow = db.queryFirst(
                "SELECT * FROM calls WHERE call_id = ?", uniqueCallId);
        assertThat(dbRow).isNotNull();
        assertThat(dbRow.get("tenant_id")).isEqualTo(tenantId);
    }
}
