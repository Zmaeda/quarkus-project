package org.acme.resource;

import static org.hamcrest.CoreMatchers.is;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

@QuarkusTest
public class PremiumCalculationResourceTest {

    @Test
    @DisplayName("正常系: 200 OK")
    public void testCalculateEndpointSuccess() {
        given()
          .contentType(ContentType.JSON)
          .body("{\"year\":\"1990\", \"month\":\"01\", \"day\":\"01\", \"gender\":\"male\"}")
        .when()
          .post("/api/calculate")
        .then()
          .statusCode(200)
          .body("status", is(200));
    }

     @Test
    @DisplayName("異常系: 400 Bad Request")
    public void testCalculateEndpointError() {
        given()
          .contentType(ContentType.JSON)
          .body("{\"year\":\"1990\", \"month\":\"\", \"day\":\"01\", \"gender\":\"male\"}")
        .when()
          .post("/api/calculate")
        .then()
          .statusCode(400)
          .body("status", is(400));
    }

    
}