package com.adhil.studentapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

/** Consistent JSON body for every error response. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(Instant timestamp, int status, String error, String message, String path,
                       Map<String, String> fieldErrors) {
}
