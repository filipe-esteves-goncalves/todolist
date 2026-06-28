This directory generates Kubernetes manifests for running `todolist` and PostgreSQL locally on Minikube.

## Prerequisites

- Minikube
- `kubectl`
- Node.js 18+
- Docker image for app: `com.filipe/todolist:0.0.1`

## Quick start

```bash
cd /home/filipe/Documents/github/todoList
minikube start

# This project has no Dockerfile; build image via Spring Boot buildpacks.
# Use plugin 4.0.4 here to avoid a Docker API issue seen with 4.0.0-M1.
./mvnw org.springframework.boot:spring-boot-maven-plugin:4.0.4:build-image -DskipTests \
  -Dspring-boot.build-image.imageName=com.filipe/todolist:0.0.1
minikube image load com.filipe/todolist:0.0.1

cd infra/cdk8s
npm install
npm run synth
kubectl apply -f dist/todo-list.k8s.yaml
```

## Verify

```bash
kubectl get pods
kubectl get svc
kubectl wait --for=condition=ready pod -l app=todolist-app --timeout=180s
minikube service todolist-app --url
```

Use the returned URL with:

- API: `http://<url>/v1/todo`
- OpenAPI: `http://<url>/v3/api-docs`
- Swagger UI: `http://<url>/swagger-ui/index.html`

For actuator (NodePort 30081):

```bash
minikube ip
curl http://$(minikube ip):30081/actuator/health
```

If `minikube service todolist-app --url` reports `SVC_UNREACHABLE`, wait for the app pod to become Ready and retry:

```bash
kubectl get pods -w
```

## Cleanup

```bash
kubectl delete -f dist/todo-list.k8s.yaml
```

