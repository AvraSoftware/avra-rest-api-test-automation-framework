package com.avra.qa.modulename.subscription.test.rest;

import com.avra.qa.common.testdata.group.GroupTestData;
import com.avra.qa.common.util.configuration.AppConfig;
import com.avra.qa.modulename.subscription.util.jpa.SubscriptionDatabaseService;
import com.avra.qa.modulename.subscription.util.rest.endpoints.SubscriptionEndpoints;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Test(alwaysRun = true, groups = {"allTests", "subscription"}, singleThreaded = true)
@ContextConfiguration(classes = {AppConfig.class})
public class GetOrderRulesTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private GroupTestData groupTestData;
    @Autowired
    private SubscriptionEndpoints subscriptionEndpoints;
    @Autowired
    private SubscriptionDatabaseService subscriptionDatabaseService;

    @AfterClass
    public void tearDown() {
        subscriptionDatabaseService.deleteAllSubscriptions();
        groupTestData.deleteAllGroups();
    }

    @SneakyThrows
    @Test
    public void should_get_subscription_order_rules() {
        //given
        UUID companyId = groupTestData.generateCompanyId();
        UUID groupId = groupTestData.sendGroupWithGivenName(companyId, "Group name");

        //when //then
        UUID subscriptionId = subscriptionEndpoints.createSubscriptionAndReturnId(companyId, groupId);

        subscriptionEndpoints.getOrderRulesDetails(subscriptionId)
                .then()
                .statusCode(OK.value())
                .assertThat().body("orderStart", is(1))
                .assertThat().body("activationDeadline", is(3))
                .assertThat().body("subscriptionId", is(notNullValue()))
                .assertThat().body("subscriptionProducts[0].visible", is(true))
                .assertThat().body("subscriptionProducts[1].visible", is(false));
    }

    @SneakyThrows
    @Test
    public void should_not_get_subscription_order_rules_when_subscription_not_exist() {
        //given
        UUID nonExistingSubscriptionId = UUID.fromString("558da95e-1234-abcd-1234-0242ac120002");

        //when //then

        subscriptionEndpoints.getOrderRulesDetails(nonExistingSubscriptionId)
                .then()
                .statusCode(NOT_FOUND.value())
                .assertThat().body("code", is("SUBSCRIPTION_ORDER_RULES_NOT_FOUND"));
    }
}
