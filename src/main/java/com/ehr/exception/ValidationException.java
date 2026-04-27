package com.ehr.exception;

/**
 * Thrown when any domain validation rule is violated.
 * Maps to HTTP 400 Bad Request via GlobalExceptionHandler.
 *
 * Keeping this as an unchecked exception avoids forcing every
 * caller to declare it in throws clauses while still being explicit
 * and distinguishable from unexpected runtime failures.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
