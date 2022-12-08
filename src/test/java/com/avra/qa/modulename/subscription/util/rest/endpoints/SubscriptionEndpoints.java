package com.avra.qa.modulename.subscription.util.rest.endpoints;

import com.avra.qa.common.util.rest.HttpRequestBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.OK;

@Component
public class SubscriptionEndpoints {

    @Autowired
    private HttpRequestBuilder httpRequestBuilder;

    public Response createSubscription(UUID companyId, UUID groupId) {
        return
                given()
                        .spec(httpRequestBuilder.initURLForPost())
                        .when()
                        .post("subscription/company/" + companyId.toString() + "/group/" + groupId.toString() + "/start")
                        .then()
                        .log().everything()
                        .contentType(ContentType.JSON)
                        .extract()
                        .response();
    }

    public UUID createSubscriptionAndReturnId(UUID companyId, UUID groupId) {
        return UUID.fromString(
                createSubscription(companyId, groupId)
                        .then()
                        .statusCode(OK.value())
                        .assertThat().body("subscriptionId", is(notNullValue()))
                        .extract().path("subscriptionId")
        );
    }

    public Response getOrderRulesDetails(UUID subscriptionId) {
        return
                given()
                        .spec(httpRequestBuilder.initURLForGet())
                        .pathParam("subscriptionId", subscriptionId.toString())
                        .when()
                        .get("/subscription/{subscriptionId}/order-rules")
                        .then()
                        .log().everything()
                        .contentType(ContentType.JSON)
                        .extract()
                        .response();
    }
}
