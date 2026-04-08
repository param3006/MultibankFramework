package com.multibank.utils;

import com.multibank.Interfaces.FrameworkConfig;
import com.multibank.config.EnvironmentConfig;
import com.multibank.models.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import lombok.extern.slf4j.Slf4j;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.Config;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;

@Slf4j
public class APIClient {

    private final RequestSpecification baseSpec;
    private final FrameworkConfig config;

    private APIClient(Builder builder) {
        this.config = ConfigManager.get();

        RequestSpecBuilder specBuilder = new RequestSpecBuilder()
                .setBaseUri(builder.baseUrl)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
//                .addFilter(new AllureRestAssured());

        if (config.logRequests()) {
            specBuilder.addFilter(new RequestLoggingFilter(LogDetail.ALL));
        }
        if (config.logResponses()) {
            specBuilder.addFilter(new ResponseLoggingFilter(LogDetail.ALL));
        }
        if (!builder.authToken.isEmpty()) {
            specBuilder.addHeader("Authorization", "Bearer " + builder.authToken);
        }
        if (!builder.apiKey.isEmpty()) {
            specBuilder.addHeader(config.apiKeyHeader(), builder.apiKey);
        }
        if (builder.extraHeaders != null) {
            specBuilder.addHeaders(builder.extraHeaders);
        }

        this.baseSpec = specBuilder.build();
        RestAssured.useRelaxedHTTPSValidation();
        log.info("ApiClient initialised → baseUrl={}", builder.baseUrl);
    }


    public Response get(String path) {
        return given(baseSpec).when().get(path).then().extract().response();
    }

    public Response get(String path, Map<String, ?> pathParams) {
        return given(baseSpec).pathParams(pathParams).when().get(path).then().extract().response();
    }

    public Response get(String path, Map<String, ?> pathParams, Map<String, ?> queryParams) {
        return given(baseSpec)
                .pathParams(pathParams)
                .queryParams(queryParams)
                .when().get(path)
                .then().extract().response();
    }

    public Response post(String path, Object body) {
        return given(baseSpec).body(body).when().post(path).then().extract().response();
    }

    public Response put(String path, Object body) {
        return given(baseSpec).body(body).when().put(path).then().extract().response();
    }

    public Response put(String path, Map<String, ?> pathParams, Object body) {
        return given(baseSpec).pathParams(pathParams).body(body).when().put(path).then().extract().response();
    }

    public Response patch(String path, Object body) {
        return given(baseSpec).body(body).when().patch(path).then().extract().response();
    }

    public Response patch(String path, Map<String, ?> pathParams, Object body) {
        return given(baseSpec).pathParams(pathParams).body(body).when().patch(path).then().extract().response();
    }

    public Response delete(String path, Map<String, ?> pathParams) {
        return given(baseSpec).pathParams(pathParams).when().delete(path).then().extract().response();
    }

    public RequestSpecification request() {
        return given(baseSpec);
    }

    public ResponseSpecification expectStatus(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .expectResponseTime(org.hamcrest.Matchers.lessThan(
                        (long) config.defaultTimeoutSeconds()), TimeUnit.SECONDS)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String baseUrl     = ConfigManager.get().baseUrl();
        private String authToken   = ConfigManager.get().authToken();
        private String apiKey      = ConfigManager.get().apiKey();
        private Map<String, String> extraHeaders;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder bearerToken(String token) {
            this.authToken = token;
            return this;
        }

        public Builder apiKey(String key) {
            this.apiKey = key;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.extraHeaders = headers;
            return this;
        }

        public APIClient build() {
            return new APIClient(this);
        }
    }
}
