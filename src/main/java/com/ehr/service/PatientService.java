package com.ehr.service;

import com.ehr.dto.ClinicalProblemRequest;
import com.ehr.dto.PatientRequest;
import com.ehr.exception.PatientNotFoundException;
import com.ehr.exception.ValidationException;
import com.ehr.model.ClinicalProblem;
import com.ehr.model.Patient;
import com.ehr.validation.SnomedValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PatientService {

    // ── In-memory storage ─────────────────────────────────────────────────────
    private final Map<String, Patient>            patients = new ConcurrentHashMap<>();
    private final Map<String, List<ClinicalProblem>> problems = new ConcurrentHashMap<>();

    // ── Thread-safe ID counters ───────────────────────────────────────────────
    private final AtomicInteger patientCounter = new AtomicInteger(10001);
    private final AtomicInteger problemCounter = new AtomicInteger(9001);

    // ── Injected validator ────────────────────────────────────────────────────
    private final SnomedValidator snomedValidator;

    private static final Set<String> VALID_GENDERS =
        Set.of("male", "female", "other", "unknown");

    public PatientService(SnomedValidator snomedValidator) {
        this.snomedValidator = snomedValidator;
    }

    // ── Create Patient ────────────────────────────────────────────────────────
    public Patient createPatient(PatientRequest request) {

        // Validate name
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ValidationException("name must not be blank");
        }

        // Validate gender (case-insensitive, store normalized lowercase)
        if (request.getGender() == null || request.getGender().isBlank()) {
            throw new ValidationException("gender must not be blank");
        }
        String gender = request.getGender().toLowerCase();
        if (!VALID_GENDERS.contains(gender)) {
            throw new ValidationException(
                "gender must be one of: male, female, other, unknown"
            );
        }

        // Validate dateOfBirth
        if (request.getDateOfBirth() == null) {
            throw new ValidationException("dateOfBirth must not be null");
        }
        if (!request.getDateOfBirth().isBefore(LocalDate.now())) {
            throw new ValidationException("dateOfBirth must be in the past");
        }

        // Generate ID and create patient
        String id = "P-" + patientCounter.getAndIncrement();
        Patient patient = new Patient(
            id,
            request.getName().trim(),
            gender,
            request.getDateOfBirth()
        );

        patients.put(id, patient);
        problems.put(id, new ArrayList<>());

        return patient;
    }

    // ── Get Patient by ID ─────────────────────────────────────────────────────
    public Optional<Patient> getPatientById(String id) {
        return Optional.ofNullable(patients.get(id));
    }

    // ── Add Clinical Problem ──────────────────────────────────────────────────
    public ClinicalProblem addProblem(String patientId, ClinicalProblemRequest request) {

        // Patient must exist
        if (!patients.containsKey(patientId)) {
            throw new PatientNotFoundException(patientId);
        }

        // Validate SNOMED coding
        snomedValidator.validate(request.getCoding());

        // Generate problem ID and store
        String problemId = "PRB-" + problemCounter.getAndIncrement();
        ClinicalProblem problem = new ClinicalProblem(
            problemId,
            patientId,
            request.getCoding(),
            request.getOnsetDate()
        );

        problems.get(patientId).add(problem);

        return problem;
    }

    // ── List Problems for Patient ─────────────────────────────────────────────
    public List<ClinicalProblem> getProblems(String patientId) {

        if (!patients.containsKey(patientId)) {
            throw new PatientNotFoundException(patientId);
        }

        return Collections.unmodifiableList(problems.get(patientId));
    }
}
