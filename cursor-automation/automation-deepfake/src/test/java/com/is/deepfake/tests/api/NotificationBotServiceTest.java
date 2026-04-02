package com.is.deepfake.tests.api;

import java.util.List;

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
 * The test acts as the Call Server: it sends a trigger request to the NB
 * and verifies the NB called the (mocked) Microsoft Graph API correctly.
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
        mockServer.stub(GraphApiStubs.sendMessageSuccess());

        NotificationTriggerRequest request = buildValidRequest();
        ApiResponse response = nbClient.triggerAlert(request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        NotificationTriggerResponse body = response.as(NotificationTriggerResponse.class);
        assertThat(body.getStatus()).isEqualTo("SENT");
        assertThat(body.getRecipientCount()).isEqualTo(1);

        mockServer.verify(1, GraphApiStubs.sendMessageWasCalled());
    }

    // ─── B1: Graph API returns 401 ───

    @Test(description = "NB: Graph API rejects with 401 -> NB returns 502 error")
    public void graphApiReturns401_nbReturnsError() {
        mockServer.stub(GraphApiStubs.sendMessageUnauthorized());

        NotificationTriggerRequest request = buildValidRequest();
        ApiResponse response = nbClient.triggerAlert(request);

        assertThat(response.getStatusCode()).isEqualTo(502);

        mockServer.verify(1, GraphApiStubs.sendMessageWasCalled());
    }

    // ─── helpers ───

    private NotificationTriggerRequest buildValidRequest() {
        return NotificationTriggerRequest.builder()
                .callId("call-123")
                .tenantId("tenant-456")
                .meetingLink("https://teams.microsoft.com/l/meetup-join/test-meeting")
                .organizerId("organizer-789")
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
                .threatLevel("HIGH")
                .build();
    }
}
