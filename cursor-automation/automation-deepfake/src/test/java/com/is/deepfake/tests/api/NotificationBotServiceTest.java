package com.is.deepfake.tests.api;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.is.deepfake.clients.NotificationBotClient;
import com.is.deepfake.dto.DetectedPerson;
import com.is.deepfake.dto.NotificationParticipant;
import com.is.deepfake.dto.NotificationTriggerRequest;
import com.is.deepfake.dto.NotificationTriggerResponse;
import com.is.deepfake.mock.GraphApiStubs;
import com.is.deepfake.testng.ServiceBaseTest;
import com.is.infra.http.ApiResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Service-level tests for the Notification Bot.
 * <p>
 * Parallel-safe: each test registers its own stubs, generates a unique callId
 * (UUID), and uses it as a correlation ID in WireMock verify calls.
 * <p>
 * WireMock lifecycle is managed by {@link ServiceBaseTest}.
 */
public class NotificationBotServiceTest extends ServiceBaseTest {

    private NotificationBotClient nbClient;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "startMockServer")
    public void setupNbClient() {
        nbClient = new NotificationBotClient();
    }

    // ─── A1: Happy Path ───

    @Test(description = "NB: trigger alert with valid payload -> Graph API receives correct message")
    public void triggerAlertWithValidPayload() {
        String uniqueCallId = UUID.randomUUID().toString();
        mockServer.stub(GraphApiStubs.sendMessageSuccess(uniqueCallId));
        NotificationTriggerRequest request = buildRequest(uniqueCallId, "HIGH");
        ApiResponse response = nbClient.triggerAlert(request);

        assertThat(response.getStatusCode()).isEqualTo(200);
        NotificationTriggerResponse body = response.as(NotificationTriggerResponse.class);
        assertThat(body.getStatus()).isEqualTo("SENT");
        assertThat(body.getRecipientCount()).isEqualTo(1);

        mockServer.verify(1, GraphApiStubs.sendMessageWasCalledWithCorrelationId(uniqueCallId));
    }

    // ─── A1b: Verify Graph request body reflects trigger fields ───

    @Test(description = "NB: Graph API request body reflects trigger payload (call id, threat, participant)")
    public void triggerAlert_graphRequestBodyReflectsTrigger() {
        String uniqueCallId = UUID.randomUUID().toString();
        mockServer.stub(GraphApiStubs.sendMessageSuccess(uniqueCallId));
        NotificationTriggerRequest request = buildRequest(uniqueCallId, "HIGH");
        ApiResponse response = nbClient.triggerAlert(request);

        assertThat(response.getStatusCode()).isEqualTo(200);
        mockServer.verify(1, GraphApiStubs.sendMessageWasCalledWithBodyContaining(uniqueCallId));
        mockServer.verify(1, GraphApiStubs.sendMessageWasCalledWithBodyContaining("HIGH"));
        mockServer.verify(1, GraphApiStubs.sendMessageWasCalledWithBodyContaining("Alice Smith"));
    }

    // ─── B1: Graph API returns 401 ───

    @Test(description = "NB: Graph API rejects with 401 -> NB returns 502 error")
    public void graphApiReturns401_nbReturnsError() {
        String uniqueCallId = UUID.randomUUID().toString();
        mockServer.stub(GraphApiStubs.sendMessageUnauthorized(uniqueCallId));
        NotificationTriggerRequest request = buildRequest(uniqueCallId, "HIGH");
        ApiResponse response = nbClient.triggerAlert(request);

        assertThat(response.getStatusCode()).isEqualTo(502);
        mockServer.verify(1, GraphApiStubs.sendMessageWasCalledWithCorrelationId(uniqueCallId));
    }

    // ─── helpers ───

    private NotificationTriggerRequest buildRequest(String callId, String threatLevel) {
        return NotificationTriggerRequest.builder()
                .callId(callId)
                .tenantId("tenant-" + UUID.randomUUID())
                .meetingLink("https://teams.microsoft.com/l/meetup-join/test-meeting")
                .organizerId("organizer-" + UUID.randomUUID())
                .internalParticipants(List.of(
                        NotificationParticipant.builder()
                                .userId("user-1")
                                .displayName("Alice Smith")
                                .build()
                ))
                .detectedPerson(DetectedPerson.builder()
                        .personId("person-1")
                        .displayName("Fake CEO")
                        .confidence(0.95)
                        .build())
                .threatLevel(threatLevel)
                .build();
    }
}
