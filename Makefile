# Makefile for building and running the todolist Docker image

IMAGE_NAME ?= com.filipe/todolist:0.0.1
MVNW ?= ./mvnw
HOST_PORT ?= 8080

.PHONY: build-image package docker-run up down wait push clean validate-task-def

# Build a container image using Spring Boot buildpacks (requires Docker)
build-image:
	$(MVNW) spring-boot:build-image -DskipTests \
		-Dspring-boot.build-image.imageName=$(IMAGE_NAME)

# Package the application (jar)
package:
	$(MVNW) package -DskipTests

# Run the image locally (requires the image to exist locally)
docker-run:
	@# check if host port is available (uses ss or lsof if present)
	@if command -v ss >/dev/null 2>&1; then \
		if ss -ltn | awk '{print $$4}' | grep -qE ":$(HOST_PORT)$$"; then \
			echo "ERROR: host port $(HOST_PORT) is already in use. Stop the process or run 'HOST_PORT=<port> make docker-run'"; exit 1; \
		fi; \
	elif command -v lsof >/dev/null 2>&1; then \
		if lsof -iTCP:$(HOST_PORT) -sTCP:LISTEN >/dev/null 2>&1; then \
			echo "ERROR: host port $(HOST_PORT) is already in use. Stop the process or run 'HOST_PORT=<port> make docker-run'"; exit 1; \
		fi; \
	fi; \
	# When running a single container (not compose), use host.docker.internal so the container
	# can connect back to a Postgres instance running on the host. On Linux Docker, --add-host
	# with host-gateway helps map this name to the host.
	docker run --rm -p $(HOST_PORT):8080 \
		--add-host=host.docker.internal:host-gateway \
		-e DB_HOST=host.docker.internal -e DB_PORT=5432 -e DB_USER=myuser -e DB_PASSWORD=secret -e DB_NAME=mydatabase \
		$(IMAGE_NAME)

# Start the app + postgres stack and wait until the app is healthy
up:
	docker compose -f compose.app.yaml up -d --build
	$(MAKE) wait

# Wait for the app container to report healthy (polls every 3s, max 90s)
wait:
	@echo "Waiting for app to be ready..."
	@timeout 90 sh -c 'until curl -sf http://localhost:8081/actuator/health > /dev/null 2>&1; do printf "."; sleep 3; done'
	@echo ""
	@echo "App is ready at http://localhost:8080"

# Stop and remove the app stack
down:
	docker compose -f compose.app.yaml down

# Push image to registry (assumes you're logged in and have permission)
push:
	docker push $(IMAGE_NAME)

# Clean build artifacts
clean:
	$(MVNW) clean

# Validate task-definition.json (requires jq)
validate-task-def:
	@echo "Validating task-definition.json..."
	@jq . task-definition.json > /dev/null && echo "  JSON syntax OK"
	@jq -e '.family                  | length > 0' task-definition.json > /dev/null || (echo "MISSING: family"; exit 1)
	@jq -e '.networkMode             | length > 0' task-definition.json > /dev/null || (echo "MISSING: networkMode"; exit 1)
	@jq -e '.containerDefinitions   | length > 0' task-definition.json > /dev/null || (echo "MISSING: containerDefinitions"; exit 1)
	@jq -e '.requiresCompatibilities | length > 0' task-definition.json > /dev/null || (echo "MISSING: requiresCompatibilities"; exit 1)
	@jq -e '.cpu                     | length > 0' task-definition.json > /dev/null || (echo "MISSING: cpu"; exit 1)
	@jq -e '.memory                  | length > 0' task-definition.json > /dev/null || (echo "MISSING: memory"; exit 1)
	@echo "  Required fields OK"
	@IMAGE=$$(jq -r '.containerDefinitions[0].image' task-definition.json); \
	  echo "  image: $$IMAGE"; \
	  echo "$$IMAGE" | grep -q '[A-Z]' && (echo "ERROR: image contains uppercase"; exit 1) || echo "  Image name OK"
	@jq -e '.requiresCompatibilities | contains(["FARGATE"])' task-definition.json > /dev/null \
	  || (echo "ERROR: requiresCompatibilities must include FARGATE"; exit 1)
	@echo "  FARGATE compatibility OK"
	@ENV_NAMES=$$(jq -r '.containerDefinitions[0].environment[].name' task-definition.json); \
	  for VAR in DB_HOST DB_PORT DB_USER DB_PASSWORD DB_NAME; do \
	    echo "$$ENV_NAMES" | grep -q "^$${VAR}$$" || (echo "MISSING env var: $$VAR"; exit 1); \
	  done
	@echo "  Env vars OK"
	@echo ""
	@echo "Summary:"
	@echo "  family    : $$(jq -r '.family' task-definition.json)"
	@echo "  image     : $$(jq -r '.containerDefinitions[0].image' task-definition.json)"
	@echo "  cpu/memory: $$(jq -r '.cpu' task-definition.json) / $$(jq -r '.memory' task-definition.json)"
	@echo "  ports     : $$(jq -r '.containerDefinitions[0].portMappings[].containerPort' task-definition.json | tr '\n' ' ')"
	@echo "task-definition.json is valid."

