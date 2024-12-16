# Spring Boot JWT Authentication with Kubernetes

This project demonstrates a Spring Boot application with JWT authentication, deployed on Kubernetes with multiple environment configurations (development and production).

## Prerequisites

- Java 17 (Amazon Corretto recommended)
- Docker
- Kubernetes (Minikube)
- kubectl
- Maven

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
└── spring-boot-with-jwt-authentication/
    └── src/
        └── main/
            └── resources/
                ├── application.yml
                ├── application-development.yml
                └── application-production.yml
```

## Development Setup

### 1. Java Configuration

Make sure you have Java 17 installed and configured:

```bash
# Set JAVA_HOME to Java 17
export JAVA_HOME=/path/to/your/java17
```

### 2. Building the Application

```bash
# Navigate to the application directory
cd spring-boot-with-jwt-authentication

# Build with Maven
./mvnw clean package -DskipTests
```

### 3. Building and Pushing Docker Image

```bash
# Get the current commit hash
COMMIT_HASH=$(git rev-parse --short HEAD)

# Build the Docker image with commit hash as tag
docker build -t louishu/practice:${COMMIT_HASH} .

# Push to Docker Hub
docker push louishu/practice:${COMMIT_HASH}
```

### 4. Updating Kubernetes Configuration

Update the image tag in your Kustomize overlay files (`k8s/overlays/development/kustomization.yaml` and `k8s/overlays/production/kustomization.yaml`):

```yaml
images:
- name: louishu/practice
  newTag: ${COMMIT_HASH}  # Replace with your actual commit hash
```

### 5. Deploying to Kubernetes

#### Development Environment

```bash
# Deploy using Kustomize
kubectl apply -k k8s/overlays/development

# Verify deployment
kubectl get all -n development
```

#### Production Environment

```bash
# Deploy using Kustomize
kubectl apply -k k8s/overlays/production

# Verify deployment
kubectl get all -n production
```

### 6. Monitoring the Deployment

```bash
# Watch the pods
kubectl get pods -n <namespace> -w

# Check pod logs
kubectl logs -f <pod-name> -n <namespace>

# Check deployment status
kubectl describe deployment spring-boot-jwt -n <namespace>
```

## API Endpoints

### Authentication Endpoints

The application comes with two pre-configured users:
1. Regular User
   - Username: `user`
   - Password: `password`
   - Role: `USER`
2. Admin User
   - Username: `admin`
   - Password: `password`
   - Role: `ADMIN`

#### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}'
```

Response:
```json
{
  "token": "<JWT_TOKEN>"
}
```

### Available Endpoints

#### Public Endpoint
```bash
curl http://localhost:8080/api/public
```

Response:
```json
{
  "message": "This is a public endpoint",
  "timestamp": "1234567890"
}
```

#### Protected Endpoint (Requires Authentication)
```bash
curl http://localhost:8080/api/protected \
  -H "Authorization: Bearer <your-jwt-token>"
```

Response:
```json
{
  "message": "This is a protected endpoint",
  "username": "user",
  "email": "user@example.com",
  "role": "USER",
  "timestamp": 1234567890
}
```

#### Admin Endpoint (Requires ADMIN Role)
```bash
curl http://localhost:8080/api/admin \
  -H "Authorization: Bearer <your-jwt-token>"
```

Response:
```json
{
  "message": "This is an admin endpoint",
  "timestamp": "1234567890"
}
```

### Error Responses

#### Unauthorized Access
```json
{
  "timestamp": "2024-12-16 03:26:23",
  "status": 401,
  "error": "Unauthorized",
  "message": "Please login first.",
  "path": "/api/protected"
}
```

#### Insufficient Privileges
```json
{
  "timestamp": "2024-12-16 03:26:23",
  "status": 403,
  "error": "Forbidden",
  "message": "Insufficient privileges.",
  "path": "/api/admin"
}
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
