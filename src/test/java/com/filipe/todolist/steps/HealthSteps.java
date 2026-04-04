package com.filipe.todolist.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class HealthSteps {

    // application.yaml configures server.port: 8080 — use the defined port for tests
    private static final int PORT = 8080;

    private HttpResponse<String> lastResponse;

    @Given("the application is running")
    public void the_application_is_running() {
        // Nothing to assert here — the Spring Boot context is started by the Cucumber config class.
    }

    @When("I GET \"{string}\"")
    public void i_get(String path) throws IOException, InterruptedException {
        String url = "http://localhost:" + PORT + path;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        lastResponse = client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    @Then("the JSON path \"{string}\" should be \"{string}\"")
    public void json_path_should_be(String jsonPath, String expected) {
        assertNotNull(lastResponse, "No HTTP response received");
        assertEquals(200, lastResponse.statusCode(), "Expected HTTP 200");
        String body = lastResponse.body();
        assertNotNull(body, "Response body was null");
        assertTrue(body.contains("\"status\""), () -> "Response body does not contain status field: " + body);
        assertTrue(body.contains('"' + expected + '"'), () -> "Response status did not match expected; body=" + body);
    }
}




