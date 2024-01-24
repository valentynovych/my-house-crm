package com.example.myhouse24admin.model.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecaptchaResponse(
        boolean success,
        String challege_ts,
        String hostname,
        @JsonProperty("error-codes")
        String[] errorCodes) {
}
