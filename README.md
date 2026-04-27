# EHR Service — Patient & Clinical Problem Microservice

A Spring Boot REST microservice for managing Patients and their SNOMED CT-coded Clinical Problems. Built as part of the DCE/LK/BT/002 assessment.

---

## Prerequisites

| Tool | Version | Check |
|------|---------|-------|
| Java | 17+ | `java -version` |
| Maven | bundled (mvnw) | no install needed |

> **No database required.** All data is stored in-memory and resets on restart.

---

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/ehr-service.git
cd ehr-service
```

### 2. Make the Maven wrapper executable (Mac/Linux only)

```bash
chmod +x mvnw
```

### 3. Run the application

**Mac / Linux:**
```bash
./mvnw spring-boot:run
```

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

You should see:
```
Started EhrServiceApplication in X seconds
```

The service is now running at **http://localhost:8080**

> **Port conflict?** If port 8080 is already in use:
> ```bash
> # Kill whatever is on 8080 (Mac/Linux)
> lsof -ti:8080 | xargs kill -9
>
> # Or run on a different port
> ./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
> ```

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/patients` | Create a patient |
| `GET` | `/api/patients/{id}` | Get patient by ID |
| `POST` | `/api/patients/{id}/problems` | Add a clinical problem |
| `GET` | `/api/patients/{id}/problems` | List patient's problems |

---

## Testing with curl

> Install [`jq`](https://stedolan.github.io/jq/) for pretty JSON output, or remove `| jq` from the commands below.

---

### 1. Create a Patient

```bash
curl -s -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","gender":"male","dateOfBirth":"1990-01-12"}' | jq
```

**Expected → 201 Created:**
```json
{
  "id": "P-10001",
  "name": "John Doe",
  "gender": "male",
  "dateOfBirth": "1990-01-12"
}
```

---

### 2. Get Patient by ID

```bash
curl -s http://localhost:8080/api/patients/P-10001 | jq
```

**Expected → 200 OK** with patient JSON.  
**Unknown ID → 404 Not Found.**

---

### 3. Add a Clinical Problem (SNOMED-coded)

```bash
curl -s -X POST http://localhost:8080/api/patients/P-10001/problems \
  -H "Content-Type: application/json" \
  -d '{
    "coding": {
      "system": "http://snomed.info/sct",
      "code": "44054006",
      "display": "Diabetes mellitus type 2"
    },
    "onsetDate": "2024-02-01"
  }' | jq
```

**Expected → 201 Created:**
```json
{
  "problemId": "PRB-9001",
  "patientId": "P-10001",
  "coding": {
    "system": "http://snomed.info/sct",
    "code": "44054006",
    "display": "Diabetes mellitus type 2"
  },
  "onsetDate": "2024-02-01"
}
```

---

### 4. List Patient Problems

```bash
curl -s http://localhost:8080/api/patients/P-10001/problems | jq
```

**Expected → 200 OK** with array of problems (empty array `[]` if none added yet).

---

## Validation Tests (all should return 400)

```bash
# Blank name
curl -s -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{"name":"","gender":"male","dateOfBirth":"1990-01-12"}' | jq

# Invalid gender
curl -s -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane","gender":"alien","dateOfBirth":"1990-01-12"}' | jq

# Date of birth in the future
curl -s -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane","gender":"female","dateOfBirth":"2030-01-01"}' | jq

# Wrong SNOMED system
curl -s -X POST http://localhost:8080/api/patients/P-10001/problems \
  -H "Content-Type: application/json" \
  -d '{"coding":{"system":"http://wrong.system","code":"44054006","display":"Diabetes"},"onsetDate":"2024-02-01"}' | jq

# Non-digit SNOMED code
curl -s -X POST http://localhost:8080/api/patients/P-10001/problems \
  -H "Content-Type: application/json" \
  -d '{"coding":{"system":"http://snomed.info/sct","code":"ABC123","display":"Diabetes"},"onsetDate":"2024-02-01"}' | jq

# Blank display
curl -s -X POST http://localhost:8080/api/patients/P-10001/problems \
  -H "Content-Type: application/json" \
  -d '{"coding":{"system":"http://snomed.info/sct","code":"44054006","display":""},"onsetDate":"2024-02-01"}' | jq
```

**Expected error shape:**
```json
{
  "status": 400,
  "error": "ValidationError",
  "message": "coding.system must be http://snomed.info/sct but was: http://wrong.system"
}
```

---

## Project Structure

```
src/main/java/com/ehr/
├── EhrServiceApplication.java       # Entry point
├── controller/
│   └── PatientController.java       # REST endpoints
├── service/
│   └── PatientService.java          # Business logic + in-memory storage
├── model/
│   ├── Patient.java
│   ├── ClinicalProblem.java
│   └── Coding.java                  # Immutable value object
├── validation/
│   └── SnomedValidator.java         # SNOMED CT validation rules
├── exception/
│   ├── ValidationException.java     # → 400
│   └── PatientNotFoundException.java # → 404
├── dto/
│   ├── PatientRequest.java
│   ├── ClinicalProblemRequest.java
│   └── ErrorResponse.java
└── advice/
    └── GlobalExceptionHandler.java  # Maps exceptions to HTTP responses
```

---

## Design Decisions

- **In-memory storage** — `ConcurrentHashMap` for thread safety; no database dependency
- **SNOMED validation** lives in `SnomedValidator` (not the controller) — Single Responsibility Principle
- **`Optional<Patient>`** returned from lookups — no null returns
- **`equals()`/`hashCode()`** on all model classes — `Patient` and `ClinicalProblem` by ID (entity identity); `Coding` by all fields (value object)
- **Gender** accepted case-insensitively, stored normalized to lowercase
- **IDs** use sequential counters (`P-10001`, `PRB-9001`) for readable, spec-matching output
