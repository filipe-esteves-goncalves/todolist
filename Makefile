# Makefile for building and running the todoList Docker image

IMAGE_NAME ?= com.filipe/todolist:0.0.1
MVNW ?= ./mvnw

.PHONY: build-image package docker-run up down push clean

# Build a container image using Spring Boot buildpacks (requires Docker)
build-image:
	$(MVNW) spring-boot:build-image -DskipTests \
		-Dspring-boot.build-image.imageName=$(IMAGE_NAME)

# Package the application (jar)
package:
	$(MVNW) package -DskipTests

# Run the image locally (requires the image to exist locally)
docker-run:
	docker run --rm -p 8080:8080 \
		-e DB_HOST=postgres -e DB_PORT=5432 -e DB_USER=myuser -e DB_PASSWORD=secret -e DB_NAME=mydatabase \
		$(IMAGE_NAME)

# Start the app + postgres stack with the compose file
up:
	docker compose -f compose.app.yaml up -d --build

# Stop and remove the app stack
down:
	docker compose -f compose.app.yaml down

# Push image to registry (assumes you're logged in and have permission)
push:
	docker push $(IMAGE_NAME)

# Clean build artifacts
clean:
	$(MVNW) clean

