# Argo CD on Minikube

This setup deploys Argo CD locally and syncs the app manifests from Git.

## Prerequisites

- Minikube running
- `kubectl`
- Repo pushed to GitHub (Argo CD pulls from Git)

## 1) Install Argo CD

```bash
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
kubectl -n argocd rollout status deployment/argocd-server --timeout=300s
```

## 2) Register this app in Argo CD

```bash
cd /home/filipe/Documents/github/todoList
kubectl apply -f infra/argocd/apps/todolist-application.yaml
kubectl -n argocd get applications
```

## 3) Access Argo CD UI

```bash
kubectl -n argocd port-forward svc/argocd-server 8085:443
```

Open:

- `https://localhost:8085`

Get initial admin password:

```bash
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d; echo
```

Username is `admin`.

## Notes

- `todolist` Application tracks `infra/k8s/base` in this repo.
- If your repo is private, configure a Git credential/SSH key in Argo CD before sync.
- After changing `infra/cdk8s/src/main.ts`, regenerate committed manifests:

```bash
cd /home/filipe/Documents/github/todoList/infra/cdk8s
npm run synth
cp dist/todo-list.k8s.yaml ../k8s/base/todo-list.yaml
```

