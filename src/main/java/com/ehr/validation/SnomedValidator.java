package com.ehr.validation;

import com.ehr.exception.ValidationException;
import com.ehr.model.Coding;
import org.springframework.stereotype.Component;

/**
 * Encapsulates all SNOMED CT coding validation rules.
 *
 * Kept in its own class (Single Responsibility Principle) so that:
 *  - Controllers stay thin (routing only)
 *  - Service stays focused on business logic
 *  - Validation rules are testable in isolation
 *
 * Marked @Component so Spring injects it wherever needed.
 */
@Component
public class SnomedValidator {

    // The only accepted coding system for this service
    private static final String SNOMED_SYSTEM = "http://snomed.info/sct";

    // SNOMED codes are numeric strings only (e.g. "44054006")
    private static final String DIGITS_ONLY = "\\d+";

    /**
     * Validates a Coding object against SNOMED CT rules.
     * Throws ValidationException with a clear message on the first violation found.
     *
     * @param coding the coding triple to validate (may be null itself)
     * @throws ValidationException if any rule is violated
     */
    public void validate(Coding coding) {

        if (coding == null) {
            throw new ValidationException("coding must not be null");
        }

        validateSystem(coding.getSystem());
        validateCode(coding.getCode());
        validateDisplay(coding.getDisplay());
    }

    // ── private rule methods ──────────────────────────────────────────────────

    private void validateSystem(String system) {
        if (system == null || system.isBlank()) {
            throw new ValidationException(
                "coding.system must not be blank"
            );
        }
        if (!SNOMED_SYSTEM.equals(system)) {
            throw new ValidationException(
                "coding.system must be " + SNOMED_SYSTEM
                + " but was: " + system
            );
        }
    }

    private void validateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new ValidationException(
                "coding.code must not be blank"
            );
        }
        if (!code.matches(DIGITS_ONLY)) {
            throw new ValidationException(
                "coding.code must contain digits only but was: " + code
            );
        }
    }

    private void validateDisplay(String display) {
        if (display == null || display.isBlank()) {
            throw new ValidationException(
                "coding.display must not be blank"
            );
        }
    }
}
