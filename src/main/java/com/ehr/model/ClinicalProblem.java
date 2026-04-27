package com.ehr.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * ClinicalProblem entity — one entry in a patient's problem list.
 *
 * Holds a reference back to the owning patientId (denormalized for easy
 * serialization in list responses without a join).
 *
 * The embedded Coding is always SNOMED-validated before this object is created;
 * validation is never the responsibility of this class.
 *
 * Entity equality: by problemId only (same reasoning as Patient).
 */
public class ClinicalProblem {

    private final String    problemId;
    private final String    patientId;
    private final Coding    coding;
    private final LocalDate onsetDate;

    public ClinicalProblem(String problemId, String patientId,
                           Coding coding, LocalDate onsetDate) {
        this.problemId = problemId;
        this.patientId = patientId;
        this.coding    = coding;
        this.onsetDate = onsetDate;
    }

    public String    getProblemId() { return problemId; }
    public String    getPatientId() { return patientId; }
    public Coding    getCoding()    { return coding;    }
    public LocalDate getOnsetDate() { return onsetDate; }


    /**
     * Entity equality by problemId only.
     * A ClinicalProblem is uniquely identified by its problemId within the system.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClinicalProblem)) return false;
        ClinicalProblem other = (ClinicalProblem) o;
        return Objects.equals(problemId, other.problemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(problemId);
    }

    @Override
    public String toString() {
        return "ClinicalProblem{problemId='" + problemId
             + "', patientId='" + patientId
             + "', coding=" + coding
             + ", onsetDate=" + onsetDate + "}";
    }
}
