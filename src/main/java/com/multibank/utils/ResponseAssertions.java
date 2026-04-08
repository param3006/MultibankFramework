package com.multibank.utils;

import io.restassured.response.Response;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ResponseAssertions {

    private final Response response;

    private ResponseAssertions(Response response) {
        this.response = response;
    }

    public static ResponseAssertions assertThat(Response response) {
        return new ResponseAssertions(response);
    }

    public ResponseAssertions hasStatusCode(int expected) {
        Assertions.assertThat(response.statusCode())
                .as("Expected HTTP status %d but got %d", expected, response.statusCode())
                .isEqualTo(expected);
        log.debug("Status assertion passed: {}", expected);
        return this;
    }

    public ResponseAssertions statusCodeIsOneOf(int... codes) {
        boolean found = false;
        for (int code : codes) {
            if (response.statusCode() == code) {
                found = true;
                break;
            }
        }
        Assertions.assertThat(found)
                .as("Expected status code to be one of %s but got %d",
                        java.util.Arrays.toString(codes), response.statusCode())
                .isTrue();
        return this;
    }

    public ResponseAssertions hasContentType(String expected) {
        Assertions.assertThat(response.contentType())
                .as("Content-Type mismatch")
                .containsIgnoringCase(expected);
        return this;
    }

    public ResponseAssertions hasHeader(String name, String value) {
        Assertions.assertThat(response.header(name))
                .as("Header '%s' mismatch", name)
                .isEqualTo(value);
        return this;
    }

    public ResponseAssertions headerExists(String name) {
        Assertions.assertThat(response.header(name))
                .as("Expected header '%s' to be present", name)
                .isNotNull();
        return this;
    }

    public FieldAssertion bodyField(String jsonPath) {
        return new FieldAssertion(jsonPath);
    }

    public ResponseAssertions bodyContains(String text) {
        Assertions.assertThat(response.body().asString())
                .as("Response body should contain '%s'", text)
                .contains(text);
        return this;
    }

    public ResponseAssertions bodyIsNotEmpty() {
        Assertions.assertThat(response.body().asString())
                .as("Response body should not be empty")
                .isNotEmpty();
        return this;
    }

    public ResponseAssertions listSizeIsGreaterThan(String jsonPath, int minSize) {
        List<?> list = response.jsonPath().getList(jsonPath);
        Assertions.assertThat(list)
                .as("List at '%s' should have more than %d items", jsonPath, minSize)
                .hasSizeGreaterThan(minSize);
        return this;
    }


//    public ResponseAssertions matchesSchema(String schemaClasspathPath) {
//        response.then().assertThat()
//                .body(JsonSc.matchesJsonSchemaInClasspath(schemaClasspathPath));
//        return this;
//    }

    // ── Performance ───────────────────────────────────────────────────────

    public ResponseAssertions respondsWithin(long duration, TimeUnit unit) {
        long limitMs = unit.toMillis(duration);
        long actualMs = response.time();
        Assertions.assertThat(actualMs)
                .as("Response time %dms exceeded limit of %dms", actualMs, limitMs)
                .isLessThanOrEqualTo(limitMs);
        return this;
    }

    public Response and() {
        return response;
    }


    public class FieldAssertion {
        private final String path;

        FieldAssertion(String path) {
            this.path = path;
        }

        public ResponseAssertions isEqualTo(Object expected) {
            Object actual = response.jsonPath().get(path);
            Assertions.assertThat(actual)
                    .as("Field '%s' mismatch", path)
                    .isEqualTo(expected);
            return ResponseAssertions.this;
        }

        public ResponseAssertions isNotNull() {
            Assertions.assertThat(Optional.ofNullable(response.jsonPath().get(path)))
                    .as("Field '%s' should not be null", path)
                    .isNotNull();
            return ResponseAssertions.this;
        }
        public ResponseAssertions isNull(){
            Assertions.assertThat(Optional.ofNullable(response.jsonPath().get(path)))
                    .as("Field '%s' should be null",path)
                    .isNull();
            return ResponseAssertions.this;
        }
        public ResponseAssertions contains(String text) {
            String value = response.jsonPath().getString(path);
            Assertions.assertThat(value)
                    .as("Field '%s' should contain '%s'", path, text)
                    .contains(text);
            return ResponseAssertions.this;
        }
    }

}
