Feature: Actuator health

  As an operator
  I want the application to report health via the actuator
  So that readiness/liveness probes can be used by orchestration platforms

  Scenario: Health endpoint returns UP
    Given the application is running
    When I GET "/actuator/health"
    Then the JSON path "$.status" should be "UP"

