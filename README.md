# Spring Boot JWT Authentication with Kubernetes

This project demonstrates a Spring Boot application with JWT authentication, deployed on Kubernetes with multiple environment configurations (development, staging, and production).

## Prerequisites

- Java 17
- Docker
- Kubernetes (Minikube)
- kubectl

## Project Structure

```
.
├── k8s/
│   ├── base/
│   │   ├── deployment.yaml
│   │   ├── service.yaml
│   │   ├── hpa.yaml
│   │   └── kustomization.yaml
│   └── overlays/
│       ├── development/
│       │   ├── kustomization.yaml
│       │   ├── namespace.yaml
│       │   └── patches/
│       │       └── deployment-patch.yaml
│       └── production/
│           ├── kustomization.yaml
│           ├── namespace.yaml
│           └── patches/
│               └── deployment-patch.yaml
├── k8s/
│   ├── development/
│   │   ├── namespace.yaml
│   │   ├── service.yaml
│   │   ├── deployment.yaml
│   │   ├── hpa.yaml
│   │   └── load-generator.yaml
│   ├── staging/
│   │   ├── namespace.yaml
│   │   ├── service.yaml
│   │   ├── deployment.yaml
│   │   └── hpa.yaml
│   └── production/
│       ├── namespace.yaml
│       ├── service.yaml
│       ├── deployment.yaml
│       └── hpa.yaml
└── spring-boot-with-jwt-authentication/
    └── src/
        └── main/
            └── resources/
                ├── application.yml
                ├── application-development.yml
                ├── application-staging.yml
                └── application-production.yml
```

## Deployment Guide

### 1. Building and Pushing Docker Image

```bash
# Build the Docker image
docker build -t louishu/practice:1.0 .

# Push to Docker Hub
docker push louishu/practice:1.0
```

### 2. Deploying to Kubernetes

#### Development Environment

```bash
# Create namespace
kubectl apply -f k8s/development/namespace.yaml

# Deploy application
kubectl apply -f k8s/development/deployment.yaml
kubectl apply -f k8s/development/service.yaml
kubectl apply -f k8s/development/hpa.yaml

# Verify deployment
kubectl get all -n development
```

#### Staging Environment

```bash
# Create namespace
kubectl apply -f k8s/staging/namespace.yaml

# Deploy application
kubectl apply -f k8s/staging/deployment.yaml
kubectl apply -f k8s/staging/service.yaml
```

#### Production Environment

```bash
# Create namespace
kubectl apply -f k8s/production/namespace.yaml

# Deploy application
kubectl apply -f k8s/production/deployment.yaml
kubectl apply -f k8s/production/service.yaml
```

### 3. Port Forwarding

To access the application locally:

```bash
# Development
kubectl port-forward svc/spring-boot-jwt-dev 8080:80 -n development

# Staging
kubectl port-forward svc/spring-boot-jwt-staging 8080:80 -n staging

# Production
kubectl port-forward svc/spring-boot-jwt-prod 8080:80 -n production
```

## Deployment with Kustomize

The project now uses Kustomize for managing Kubernetes configurations across different environments.

### Directory Structure
```
k8s/
├── base/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── hpa.yaml
│   └── kustomization.yaml
└── overlays/
    ├── development/
    │   ├── kustomization.yaml
    │   ├── namespace.yaml
    │   └── patches/
    │       └── deployment-patch.yaml
    └── production/
        ├── kustomization.yaml
        ├── namespace.yaml
        └── patches/
            └── deployment-patch.yaml
```

### Deployment Commands

1. Build and push Docker image:
```bash
# Get current git commit hash for tag
IMAGE_TAG=$(git rev-parse --short HEAD)

# Build image
docker build -t louishu/practice:${IMAGE_TAG} .

# Push image
docker push louishu/practice:${IMAGE_TAG}

# Update image tag in kustomization files
sed -i '' "s/newTag: .*/newTag: ${IMAGE_TAG}/" k8s/overlays/*/kustomization.yaml
```

2. Deploy to development:
```bash
kubectl apply -k k8s/overlays/development
```

3. Deploy to production:
```bash
kubectl apply -k k8s/overlays/production
```

### Verify Deployment
```bash
# Check deployment status
kubectl get all -n development
# or
kubectl get all -n production

# View logs
kubectl logs -n development -l app=spring-boot-jwt
```

### Cleanup
```bash
# Remove development environment
kubectl delete -k k8s/overlays/development

# Remove production environment
kubectl delete -k k8s/overlays/production
```

## Testing Guide

### 1. Authentication Endpoints

#### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

#### Protected Endpoint (with JWT)
```bash
curl http://localhost:8080/api/protected \
  -H "Authorization: Bearer <your-jwt-token>"
```

### 2. Default Users

- Admin User:
  - Username: admin
  - Password: password
- Regular User:
  - Username: user
  - Password: password

### 3. HPA (Horizontal Pod Autoscaler) Testing

```bash
# Start the load generator
kubectl apply -f k8s/development/load-generator.yaml

# Monitor HPA
kubectl get hpa spring-boot-jwt-hpa -n development --watch

# Monitor pods
kubectl get pods -n development --watch

# Stop the load test
kubectl delete pod load-generator -n development
```

## Monitoring and Debugging

### View Logs

```bash
# Get pod name
kubectl get pods -n development

# View logs
kubectl logs <pod-name> -n development
```

### Check Resources

```bash
# View resource usage
kubectl top pods -n development
```

### Dashboard Access

```bash
# Start Kubernetes dashboard
minikube dashboard
```

## Environment-Specific Configurations

- Development:
  - Logging Level: DEBUG
  - HPA: 1-3 replicas, 50% CPU threshold
  
- Staging:
  - Logging Level: INFO
  
- Production:
  - Logging Level: WARN
  
Each environment has its own namespace and configuration settings for better isolation and management.

## Request Tracking with X-Request-ID

This application implements request tracking using X-Request-ID for distributed tracing and troubleshooting.

### Implementation

#### Backend
- Automatically generates X-Request-ID if not provided in the request
- Includes Request-ID in all log entries
- Returns X-Request-ID in response headers

#### Frontend Implementation Example
```typescript
// Add X-Request-ID to all API requests
api.interceptors.request.use((config) => {
  config.headers['X-Request-ID'] = uuidv4();
  return config;
});
```

### Troubleshooting Use Cases

1. **Error Tracking**
```bash
# Search logs by Request-ID
kubectl logs -n production -l app=spring-boot-jwt --grep="550e8400-e29b-41d4"
```

2. **Performance Analysis**
```
Request-ID: 550e8400-e29b-41d4
11:25:30.100 API received
11:25:30.200 DB query started
11:25:32.300 DB query completed
11:25:32.400 Response sent
→ Identifies 2-second DB query delay
```

3. **Cross-Service Tracing**
```
Frontend → API Gateway → Service A → Service B
   ↓          ↓            ↓           ↓
X-Request-ID: 550e8400-e29b-41d4-a716-446655440000
```

### Best Practices
- Always include X-Request-ID in API requests from frontend
- Log Request-ID with relevant context
- Include Request-ID in error responses for easier debugging
- Store Request-ID for customer support reference

## Cleanup

```bash
# Delete development environment
kubectl delete namespace development

# Delete staging environment
kubectl delete namespace staging

# Delete production environment
kubectl delete namespace production
```

## Security Notes

1. JWT secrets are environment-specific
2. Production deployments should use Kubernetes Secrets for sensitive data
3. Service type is set to ClusterIP for internal access only
4. External access is managed through port forwarding or ingress (not included)
