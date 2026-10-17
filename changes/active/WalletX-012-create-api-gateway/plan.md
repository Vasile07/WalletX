# Plan for WalletX-012: Create API Gateway

## Proposed implementation

Create a dedicated Spring Cloud Gateway module that sits in front of the existing user, wallet, and transfer services.

### Scope and affected areas

- Add a new `api-gateway` Gradle module to the backend workspace.
- Configure static routes for `/auth/**`, `/users/**`, `/wallets/**`, and `/transfers/**` to the corresponding backend service.
- Validate bearer JWTs at the gateway and preserve the user identity while forwarding authorized requests.
- Add CORS and correlation IDs for frontend compatibility and request tracing.
- Expose a gateway health endpoint and add focused tests to confirm the module starts and the protected routes are rejected without valid authentication.

### Implementation steps

1. Review the existing service ports and JWT conventions used by the backend services.
2. Add the gateway module, dependencies, and runtime configuration for each upstream service URL.
3. Add route definitions and security filters so public/auth endpoints are allowed while protected routes require a bearer token.
4. Add a correlation ID filter and CORS configuration to centralize API concerns at the boundary.
5. Run the module tests and a focused Gradle build to verify startup and validation behavior.

### Verification

- Run the new gateway module tests.
- Confirm `/actuator/health` is available and protected routes return `401` without a valid bearer token.
- Confirm the config compiles as part of the backend Gradle build.

### Questions / blockers

- No blockers at the moment; the user, wallet, and transfer services already follow the same JWT contract and use distinct ports.
