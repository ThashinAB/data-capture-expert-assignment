package com.ehr.advice;

import com.ehr.dto.ErrorResponse;
import com.ehr.exception.PatientNotFoundException;
import com.ehr.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - Validation failures (patient fields, SNOMED coding)
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(ValidationException ex) {
        return new ErrorResponse(400, "ValidationError", ex.getMessage());
    }

    // 404 - Patient not found
    @ExceptionHandler(PatientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(PatientNotFoundException ex) {
        return new ErrorResponse(404, "NotFound", ex.getMessage());
    }

    // 500 - Unexpected failures
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneral(Exception ex) {
        return new ErrorResponse(500, "InternalServerError", "An unexpected error occurred");
    }
}
