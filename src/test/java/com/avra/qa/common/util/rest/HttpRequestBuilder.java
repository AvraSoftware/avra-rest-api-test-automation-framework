package com.avra.qa.common.util.rest;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HttpRequestBuilder {

    private final String httpHeaderAccept;
    private final String httpHeaderContentType;
    private final String httpHeaderContentTypeForApplicationHealthEndpoint;

    @Autowired
    private ApplicationHostProvider applicationHostProvider;

    public HttpRequestBuilder(
            @Value("${httpHeaderAccept}") String httpHeaderAccept,
            @Value("${httpHeaderContentType}") String httpHeaderContentType,
            @Value("${httpHeaderContentTypeForApplicationHealthEndpoint}") String httpHeaderContentTypeForApplicationHealthEndpoint
    ) {
        this.httpHeaderAccept = httpHeaderAccept;
        this.httpHeaderContentType = httpHeaderContentType;
        this.httpHeaderContentTypeForApplicationHealthEndpoint = httpHeaderContentTypeForApplicationHealthEndpoint;
    }

    public RequestSpecification initURLForGet() {
        ApplicationHostProvider.ApplicationHost applicationHost = applicationHostProvider.chooseOneApplicationHost();
        return new RequestSpecBuilder()
                .setAccept(httpHeaderAccept)
                .setBaseUri(applicationHost.getHost())
                .setPort(applicationHost.getPort())
                .addFilter(new AllureRestAssured())
                .build();
    }

    public RequestSpecification initURLForPost() {
        ApplicationHostProvider.ApplicationHost applicationHost = applicationHostProvider.chooseOneApplicationHost();
        return new RequestSpecBuilder()
                .setAccept(httpHeaderAccept)
                .setContentType(httpHeaderContentType)
                .setBaseUri(applicationHost.getHost())
                .setPort(applicationHost.getPort())
                .addFilter(new AllureRestAssured())
                .build();
    }
}
