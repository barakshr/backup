package com.is.deepfake.tests.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.is.deepfake.clients.NotificationBotClient;
import com.is.deepfake.dto.DetectedPerson;
import com.is.deepfake.dto.NotificationParticipant;
import com.is.deepfake.dto.NotificationTriggerRequest;
import com.is.deepfake.dto.NotificationTriggerResponse;
import com.is.deepfake.mock.GraphApiStubs;
import com.is.deepfake.testng.ServiceBaseTest;
import com.is.infra.http.ApiResponse;

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

    /* In this TC we test what happens when the a NB recive a  sucess response from the graf api , then the NB   process it and return it to the client (test) so we can assert it 
    /* The main flow is : test->NB endpoint->wiremock endpoint ->wiremock ( graf api)  response ->nb-> test ->assertation
     * The body content we send to the NB is not that importent (only that the shcema is correct) because the response from the wiremock is alrerady predefined 
     * and it will accuer when the NB do a the http request to  the wiremock with the endpoint  wirmock ( microsoft graph api) provied to it ( gragh api swagger)
     * we alos need to know the response from the graf api client so we can predifine it in the wiremock stub
     * 
     * 1. we register the "sendMessageSuccess" stub method to the mockServer.stub that execpt mapping builder object 
     * (it is possible to register more then 1 method the "mockServer.stub" in case if the service we test  do more then a single api request to the mock server)
     * 2. see explnation of "GraphApiStubs.sendMessageSuccess" under that method on how it handels the endpoint request ,  in the class "GraphApiStubs"
     * 3  we send the post request to the notfication bot client "nbClient.triggerAlert(request)"
     * 4. the notification  bot do it own work on the request and then do an http request to the mock server (fake microsoft graph api)
     * 5. the mock server will return predifined response to the nb
     * 6. the NB will process it and return response to the request we did in step 3
     * 7.then we assert it
     */

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

        List<LoggedRequest> graphRequests = mockServer.findAll(
                GraphApiStubs.sendMessageWasCalledWithCorrelationId(uniqueCallId));
        assertThat(graphRequests).hasSize(1);

        String graphBody = graphRequests.get(0).getBodyAsString();
        assertThat(graphBody)
                .contains(uniqueCallId)
                .contains("HIGH")
                .contains("Alice Smith");
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
                                .build()))
                .detectedPerson(DetectedPerson.builder()
                        .personId("person-1")
                        .displayName("Fake CEO")
                        .confidence(0.95)
                        .build())
                .threatLevel(threatLevel)
                .build();
    }
}
