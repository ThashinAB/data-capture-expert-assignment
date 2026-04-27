package com.ehr.controller;

import com.ehr.dto.ClinicalProblemRequest;
import com.ehr.dto.PatientRequest;
import com.ehr.exception.PatientNotFoundException;
import com.ehr.model.ClinicalProblem;
import com.ehr.model.Patient;
import com.ehr.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // 1) POST /api/patients — Create Patient
    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody PatientRequest request) {
        Patient created = patientService.createPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 2) GET /api/patients/{id} — Get Patient by ID
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable String id) {
        return patientService.getPatientById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new PatientNotFoundException(id));
    }

    // 3) POST /api/patients/{id}/problems — Add Clinical Problem
    @PostMapping("/{id}/problems")
    public ResponseEntity<ClinicalProblem> addProblem(
            @PathVariable String id,
            @RequestBody ClinicalProblemRequest request) {

        ClinicalProblem problem = patientService.addProblem(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(problem);
    }

    // 4) GET /api/patients/{id}/problems — List Patient Problems
    @GetMapping("/{id}/problems")
    public ResponseEntity<List<ClinicalProblem>> getProblems(@PathVariable String id) {
        List<ClinicalProblem> list = patientService.getProblems(id);
        return ResponseEntity.ok(list);
    }
}
