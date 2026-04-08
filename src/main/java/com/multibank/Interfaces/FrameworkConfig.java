package com.multibank.Interfaces;

import org.aeonbits.owner.Config;

public interface FrameworkConfig extends Config {

    @Key("BASE_URL")
    @DefaultValue("https://testuatapi.pay10.asia")
    String baseUrl();

    @Key("ENV")
    @DefaultValue("qa")
    String env();

    @Key("DEFAULT_TIMEOUT_SECONDS")
    @DefaultValue("30")
    int defaultTimeoutSeconds();

    @Key("LOG_REQUESTS")
    @DefaultValue("true")
    boolean logRequests();

    @Key("LOG_RESPONSES")
    @DefaultValue("true")
    boolean logResponses();

    @Key("AUTH_TOKEN")
    @DefaultValue("")
    String authToken();

    @Key("API_KEY_HEADER")
    @DefaultValue("x-api-key")
    String apiKeyHeader();

    @Key("API_KEY")
    @DefaultValue("")
    String apiKey();

    @Key("MAX_RETRY_COUNT")
    @DefaultValue("2")
    int maxRetryCount();

    @Key("RETRY_DELAY_MS")
    @DefaultValue("500")
    int retryDelayMs();
}
