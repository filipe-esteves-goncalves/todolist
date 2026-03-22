# Makefile for building and running the todolist Docker image

IMAGE_NAME ?= com.filipe/todolist:0.0.1
MVNW ?= ./mvnw
HOST_PORT ?= 8080

.PHONY: build-image package docker-run up down wait push clean

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
	@timeout 90 sh -c 'until docker inspect --format="{{.State.Health.Status}}" todolist-app-1 2>/dev/null | grep -q "healthy"; do printf "."; sleep 3; done'
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

