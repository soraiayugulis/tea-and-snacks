# Next Steps

## 1. Authentication Enhancements

- [ ] **Refresh Token Rotation**: Implement secure token rotation with token family tracking
- [ ] **Email Verification**: Add email confirmation flow for new registrations (optional)
- [ ] **Password Reset**: Self-service password recovery via secure email tokens
- [ ] **Brute Force Protection**: Implement progressive delay and account lockout policies
- [ ] **Audit Trail**: Structured logging for security events (login/logout, sensitive operations)

## 2. Role-Based Access Control (RBAC) ✅ (2026-05-18)

- [x] **Endpoint Protection**: Apply `@RolesAllowed` annotations to destructive operations (DELETE endpoints) - Protected Tea, Snack, Sauce DELETE endpoints
- [x] **Manager Role**: Introduce intermediate privilege level between USER and ADMIN - Role hierarchy USER < MANAGER < ADMIN implemented
- [x] **User Management API**: Admin endpoints for role assignment and modification - PUT /users/{username}/roles, POST /users/{username}/activate|deactivate
- [x] **Account Lifecycle**: Soft delete (deactivation) and reactivation endpoints - Full lifecycle management with privilege checks

## 3. Analytics & Reporting

- [ ] **Aggregation Endpoints**: Category distribution, dietary statistics (vegan count), inventory metrics
- [ ] **Dashboard Metrics**: Consolidated overview endpoint with key performance indicators
- [ ] **Access Analytics**: Track and report most accessed/queried products

## 4. Search Capabilities

- [ ] **Full-Text Search**: PostgreSQL `tsvector` implementation across products (name, description, ingredients)
- [ ] **Autocomplete API**: Optimized prefix search with caching for real-time suggestions

## 5. Media Management

- [ ] **Image Schema**: Add `image_url` and `image_metadata` fields to product entities
- [ ] **Upload API**: Multipart endpoint with validation (format, size, dimensions)
- [ ] **Storage Integration**: Abstract storage layer supporting local/S3/MinIO backends

## 6. Test Coverage ✅ (2026-05-18)

- [x] **Integration Tests**: JWT authentication flows (token issuance, validation, expiration) - AuthTestHelper for token management
- [x] **User API Tests**: CRUD operations and edge cases for user management - All endpoints tested with RestAssured
- [x] **Authorization Tests**: Verify 403 responses for insufficient privileges - RBAC tests for all role combinations
- [x] **Security Tests**: Unauthorized access attempts, token tampering, injection attempts - DELETE endpoints protected

## 7. Infrastructure & DevOps

- [ ] **CI/CD Pipeline**: GitHub Actions workflow for build, test, and artifact publishing
- [ ] **Optimized Container**: Multi-stage Docker build with distroless final image
- [ ] **Orchestration**: Kubernetes manifests (deployment, service, ingress, HPA)
- [ ] **Observability**: Prometheus metrics endpoint and Grafana dashboards
- [ ] **Centralized Logging**: Structured JSON logging with correlation IDs

## 8. API Evolution

- [ ] **Versioning Strategy**: URL-based versioning (`/v1/`, `/v2/`) for backward compatibility
- [ ] **OpenAPI Documentation**: Keep Swagger annotations synchronized with implementation
