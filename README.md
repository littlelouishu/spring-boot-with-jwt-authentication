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
│   ├── development/
│   │   ├── namespace.yaml
│   │   ├── service.yaml
│   │   ├── deployment.yaml
│   │   ├── hpa.yaml
│   │   └── load-generator.yaml
│   ├── staging/
│   │   ├── namespace.yaml
│   │   └── service.yaml
│   └── production/
│       ├── namespace.yaml
│       └── service.yaml
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
