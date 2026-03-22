# AI Agent Instruction: GitHub Actions CI/CD with LocalStack Smoke Tests

This document serves as a specification for the AI Agent to generate and configure GitHub Action workflows for the current Spring Boot project.

## Project Context

* **App Name:** `todoList` (from `pom.xml`)
* **Main Port:** 8080 (API), 8081 (Actuator)
* **Database:** PostgreSQL (Hardcoded credentials: `myuser`/`secret`)
* **Build Tool:** Maven / Spring Boot Buildpacks
* **Visibility:** Public Repository (Learning project, no Secrets used)

## Task 1: Image Publication Workflow

**Goal:** Create `.github/workflows/publish.yml` to build and push the image to GHCR.

### Requirements

1. **Trigger:** On push to `main` branch or manual `workflow_dispatch`.
2. **Permissions:** Must have `packages: write` and `contents: read`.
3. **Auth:** Use the automatic `${{ secrets.GITHUB_TOKEN }}`.
4. **Build Step:** Use the Maven command:
   ```
   ./mvnw spring-boot:build-image -DskipTests -Dspring-boot.build-image.imageName=ghcr.io/${{ github.repository_owner }}/todo-list:latest
   ```
5. **Login:** Use `docker/login-action@v3` with:
   * **Registry:** `ghcr.io`
   * **Username:** `${{ github.actor }}`
   * **Password:** `${{ secrets.GITHUB_TOKEN }}`
6. **Push:** Push the tagged image to `ghcr.io/${{ github.repository_owner }}/todo-list:latest`.

## Task 2: Smoke Test Workflow (LocalStack + ECS)

**Goal:** Create `.github/workflows/smoke-test.yml` to verify the image in an emulated AWS environment.

### Requirements

1. **Trigger:** After the publish workflow completes (`workflow_run`) or manually.
2. **Setup Services:**
   * **LocalStack:** Use `localstack/localstack` image. Enable `ecs`, `ec2`, and `iam`.
   * **Postgres:** Use `postgres:15-alpine` as a GitHub Actions service container.
3. **ECS Deployment:**
   * Install `awscli-local`.
   * Create a local ECS Cluster: `awslocal ecs create-cluster --cluster-name todo-cluster`.
   * Register the Task Definition (using the template from Task 3).
   * Run the task: `awslocal ecs run-task --cluster todo-cluster --task-definition todo-task`.
4. **CRUD Smoke Test:**
   * **Health Check:** Wait for the app to be ready: Poll `http://localhost:8081/actuator/health` until status is `UP`.
   * **POST:** Create a todo item:
     ```
     curl -X POST http://localhost:8080/todo -H "Content-Type: application/json" -d '{"title":"Smoke Test", "description": "Testing CI"}'
     ```
   * **GET:** Verify the item exists and the response contains `"Smoke Test"`.
5. **Hardcoded Config:** Use hardcoded strings for DB environment variables (`DB_USER=myuser`, etc.) directly in the workflow/task-def.

## Task 3: ECS Task Definition Template

**Goal:** Provide a `task-definition.json` for the agent to use.

```json
{
  "family": "todo-task",
  "networkMode": "awsvpc",
  "containerDefinitions": [
    {
      "name": "todo-app",
      "image": "ghcr.io/<OWNER>/todo-list:latest",
      "essential": true,
      "portMappings": [
        {
          "containerPort": 8080,
          "hostPort": 8080,
          "protocol": "tcp"
        },
        {
          "containerPort": 8081,
          "hostPort": 8081,
          "protocol": "tcp"
        }
      ],
      "environment": [
        { "name": "DB_HOST", "value": "postgres" },
        { "name": "DB_PORT", "value": "5432" },
        { "name": "DB_USER", "value": "myuser" },
        { "name": "DB_PASSWORD", "value": "secret" },
        { "name": "DB_NAME", "value": "mydatabase" }
      ]
    }
  ],
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512"
}
```

## Execution Instructions for AI Agent

1. **Step 1:** Generate `.github/workflows/publish.yml`.
2. **Step 2:** Generate `task-definition.json` (replacing `<OWNER>` with the actual repo owner).
3. **Step 3:** Generate `.github/workflows/smoke-test.yml`. Use a simple run block for the curl commands.
4. **Step 4 (Important):** Do not use custom secrets except for the built-in `GITHUB_TOKEN`. Hardcode all other values.