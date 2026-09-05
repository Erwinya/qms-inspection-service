# qms-inspection-service

REST API for **quality inspections** in a Quality Management System.

Repository: [Erwinya/qms-inspection-service](https://github.com/Erwinya/qms-inspection-service)

Companion service: [qms-ncr-service](https://github.com/Erwinya/qms-ncr-service) (Nonconformance Reports)

## What it does

- Create planned inspections with lot/part context and inspector
- Track workflow status and final result (PASS / FAIL / CONDITIONAL)
- Optionally link a related NCR number when a failure is escalated
- List/filter by status or result
- Expose `/actuator/health` for local and container probes

```text
PLANNED → IN_PROGRESS → COMPLETED
    ↓           ↓
 CANCELLED  CANCELLED
```

Completing an inspection requires a final result.

## Requirements

- Java 17+
- Maven 3.9+

## Run locally (H2 by default)

```powershell
.\mvnw.cmd spring-boot:run
```

Open:

- Swagger UI: http://localhost:8083/swagger-ui.html
- Health: http://localhost:8083/actuator/health
- API root redirects to Swagger

## Example

```bash
curl -s http://localhost:8083/actuator/health
```

```http
POST /api/v1/inspections
Content-Type: application/json

{
  "title": "Incoming thickness check",
  "checklistSummary": "Measure thickness at 3 points; compare to limits.",
  "lotNumber": "LOT-4821",
  "partNumber": "WAFER-A",
  "inspector": "qa.inspector"
}
```

```http
PUT /api/v1/inspections/{id}/status
Content-Type: application/json

{
  "status": "COMPLETED",
  "result": "FAIL",
  "relatedNcrNumber": "NCR-2026-0001",
  "note": "Out of tolerance; escalated to NCR"
}
```

## Docker (PostgreSQL)

```bash
docker compose up --build
```

Compose sets `SPRING_PROFILES_ACTIVE=docker` so the API uses PostgreSQL.

Published host ports:

| Service | Host port | Container |
|---------|-----------|-----------|
| API | 8083 | 8083 |
| Postgres | 5435 | 5432 |

Hybrid run (API on host, DB in Compose):

```powershell
docker compose up db -d
$env:SPRING_PROFILES_ACTIVE="docker"
$env:DB_URL="jdbc:postgresql://localhost:5435/qms_inspection"
$env:DB_USERNAME="qms"
$env:DB_PASSWORD="qms"
.\mvnw.cmd spring-boot:run
```

## Native companion (C)

[`native/inspection-check`](native/inspection-check) is a small C11 CLI that checks measurement values against a min/max tolerance window. It complements the Java API for local/scripted inspection evaluation.

```bash
cd native/inspection-check
make
./inspection-check --min 0.48 --max 0.52 --file samples/measures.txt
```

On Windows with MinGW/MSYS2: `build.bat`

## Tests

```powershell
.\mvnw.cmd test
```

## Tech

- Spring Boot 3.5 / Java 17
- Spring Data JPA
- Spring Boot Actuator
- springdoc OpenAPI
- H2 by default (`local`) / PostgreSQL via Docker (`docker`)
- C11 companion CLI (`native/inspection-check`)

## License

MIT