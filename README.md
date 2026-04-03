# todolist

Small Spring Boot REST API for managing TODO items.

## Summary

This project is a simple TODO list service implemented with Spring Boot (Spring Boot 4 / Java 25). 
It exposes a REST API under `/todo` and persists data to PostgreSQL using Spring Data JPA.

The project uses Lombok for logging and boilerplate reduction. 
The controller uses `@Slf4j` (Lombok) which logs through SLF4J (Logback provided by Spring Boot starters).

## Requirements

- Java 25 (configured in `pom.xml`)
- Maven 3.8+ (or newer)
- PostgreSQL (or run via Docker Compose included in the repo)

## Build

From the project root:

```bash
mvn -U -DskipTests package
```

This produces a runnable jar in `target/` (e.g. `target/todolist-1.0.0.jar`).

## Run

Run locally (requires a PostgreSQL instance accessible using the defaults or configured via env vars):

```bash
# set DB connection if not using defaults
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=mydatabase
export DB_USER=myuser
export DB_PASSWORD=secret

mvn spring-boot:run
# or
java -jar target/todolist-1.0.0.jar
```

Run with Docker Compose (repo contains `compose.yaml` / `compose.app.yaml`):

```bash
docker compose -f compose.yaml up --build
```

## Tests

Run unit tests with:

```bash
mvn test
```

## API (quick examples)

Base path: `http://localhost:8080/todo`

- GET /todo — list all todos
- GET /todo/{id} — get a specific todo
- POST /todo — create a todo
- PUT /todo/{id} — update a todo
- DELETE /todo/{id} — delete a todo

Example `POST` payload (JSON):

```json
{
  "title": "Buy milk",
  "description": "2 liters",
  "completed": false
}
```

Example curl requests:

```bash
# create
curl -X POST -H "Content-Type: application/json" -d '{"title":"Buy milk","description":"2 liters","completed":false}' http://localhost:8080/todo

# list
curl http://localhost:8080/todo
```

## Lombok and SLF4J notes

- Lombok is already added to `pom.xml` and configured as an annotation processor for the Maven build. The controller uses Lombok's `@Slf4j` annotation to get an SLF4J logger instance.
- SLF4J implementation (Logback) is provided transitively by Spring Boot starters. No extra logging dependency is required.

If Lombok annotations (like `@Slf4j`) are not recognized in your IDE:

1. Install the Lombok plugin (for IntelliJ IDEA use the 'Lombok' plugin).
2. Enable annotation processing in IntelliJ: `Settings` → `Build, Execution, Deployment` → `Compiler` → `Annotation Processors` → check `Enable annotation processing`.

Adjust logging levels via `src/main/resources/application.yaml` or environment properties, for example:

```yaml
# set debug logging for application package
logging:
  level:
    com.filipe.todolist: DEBUG
```

## Troubleshooting

- If the application cannot connect to the database, verify env vars or `compose.yaml` service. The default JDBC URL is configured in `src/main/resources/application.yaml`.
- If Lombok-generated logging methods (e.g. `log.info`) cause compile errors, ensure the Maven `maven-compiler-plugin` configuration includes the Lombok annotation processor (this project already configures it in `pom.xml`).

## Contributing

Feel free to open issues or submit pull requests.

---
Generated README to help building, running and debugging the project (including Lombok/SLF4J notes).

