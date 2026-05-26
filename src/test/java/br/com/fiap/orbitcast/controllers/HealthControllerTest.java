package br.com.fiap.orbitcast.controllers;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class HealthControllerTest {
    @Test
    void testHelloEndpoint() {
        given()
          .when().get("/health")
          .then()
             .statusCode(200)
             .body(is("OrbitCast API online"));
    }

}
