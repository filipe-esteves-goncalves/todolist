import { ApiObject, App, Chart } from 'cdk8s';

const app = new App();
const chart = new Chart(app, 'todo-list');

new ApiObject(chart, 'DbSecret', {
  apiVersion: 'v1',
  kind: 'Secret',
  metadata: { name: 'db-secret' },
  type: 'Opaque',
  stringData: {
    DB_PASSWORD: 'secret',
    POSTGRES_PASSWORD: 'secret'
  }
});

new ApiObject(chart, 'PostgresPvc', {
  apiVersion: 'v1',
  kind: 'PersistentVolumeClaim',
  metadata: { name: 'postgres-data' },
  spec: {
    accessModes: ['ReadWriteOnce'],
    resources: {
      requests: { storage: '1Gi' }
    }
  }
});

new ApiObject(chart, 'PostgresDeployment', {
  apiVersion: 'apps/v1',
  kind: 'Deployment',
  metadata: {
    name: 'postgres',
    labels: { app: 'postgres' }
  },
  spec: {
    replicas: 1,
    selector: { matchLabels: { app: 'postgres' } },
    template: {
      metadata: { labels: { app: 'postgres' } },
      spec: {
        containers: [
          {
            name: 'postgres',
            image: 'postgres:15-alpine',
            imagePullPolicy: 'IfNotPresent',
            ports: [{ containerPort: 5432 }],
            env: [
              { name: 'POSTGRES_DB', value: 'mydatabase' },
              { name: 'POSTGRES_USER', value: 'myuser' },
              {
                name: 'POSTGRES_PASSWORD',
                valueFrom: {
                  secretKeyRef: { name: 'db-secret', key: 'POSTGRES_PASSWORD' }
                }
              }
            ],
            volumeMounts: [
              {
                name: 'postgres-data',
                mountPath: '/var/lib/postgresql/data'
              }
            ],
            readinessProbe: {
              exec: {
                command: ['sh', '-c', 'pg_isready -U myuser -d mydatabase']
              },
              initialDelaySeconds: 5,
              periodSeconds: 10
            }
          }
        ],
        volumes: [
          {
            name: 'postgres-data',
            persistentVolumeClaim: { claimName: 'postgres-data' }
          }
        ]
      }
    }
  }
});

new ApiObject(chart, 'PostgresService', {
  apiVersion: 'v1',
  kind: 'Service',
  metadata: { name: 'postgres' },
  spec: {
    selector: { app: 'postgres' },
    ports: [{ name: 'postgres', port: 5432, targetPort: 5432 }]
  }
});

new ApiObject(chart, 'TodoDeployment', {
  apiVersion: 'apps/v1',
  kind: 'Deployment',
  metadata: {
    name: 'todolist-app',
    labels: { app: 'todolist-app' }
  },
  spec: {
    replicas: 1,
    selector: { matchLabels: { app: 'todolist-app' } },
    template: {
      metadata: { labels: { app: 'todolist-app' } },
      spec: {
        containers: [
          {
            name: 'app',
            image: 'com.filipe/todolist:0.0.1',
            imagePullPolicy: 'IfNotPresent',
            ports: [
              { name: 'http', containerPort: 8080 },
              { name: 'actuator', containerPort: 8081 }
            ],
            env: [
              { name: 'DB_HOST', value: 'postgres' },
              { name: 'DB_PORT', value: '5432' },
              { name: 'DB_USER', value: 'myuser' },
              { name: 'DB_NAME', value: 'mydatabase' },
              {
                name: 'DB_PASSWORD',
                valueFrom: {
                  secretKeyRef: { name: 'db-secret', key: 'DB_PASSWORD' }
                }
              }
            ],
            readinessProbe: {
              httpGet: { path: '/actuator/health/readiness', port: 8081 },
              initialDelaySeconds: 20,
              periodSeconds: 10
            },
            livenessProbe: {
              httpGet: { path: '/actuator/health/liveness', port: 8081 },
              initialDelaySeconds: 30,
              periodSeconds: 15
            }
          }
        ]
      }
    }
  }
});

new ApiObject(chart, 'TodoService', {
  apiVersion: 'v1',
  kind: 'Service',
  metadata: { name: 'todolist-app' },
  spec: {
    type: 'NodePort',
    selector: { app: 'todolist-app' },
    ports: [
      {
        name: 'http',
        port: 8080,
        targetPort: 8080,
        nodePort: 30080
      },
      {
        name: 'actuator',
        port: 8081,
        targetPort: 8081,
        nodePort: 30081
      }
    ]
  }
});

app.synth();

