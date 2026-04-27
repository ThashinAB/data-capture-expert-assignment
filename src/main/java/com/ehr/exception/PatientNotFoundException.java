package com.ehr.exception;

/**
 * Thrown when a patient lookup by id yields no result.
 * Maps to HTTP 404 Not Found via GlobalExceptionHandler.
 *
 * Separate from ValidationException to keep HTTP mapping unambiguous:
 * - ValidationException  → 400
 * - PatientNotFoundException → 404
 */
public class PatientNotFoundException extends RuntimeException {

    private final String patientId;

    public PatientNotFoundException(String patientId) {
        super("Patient not found with id: " + patientId);
        this.patientId = patientId;
    }

    public String getPatientId() {
        return patientId;
    }
}
