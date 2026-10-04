# WalletX Technology Stack

This document records the current technology decisions for WalletX so future sessions can continue implementation consistently.

## Core Technologies

| Area | Decision | Purpose |
|---|---|---|
| Backend | Java with Spring Boot | Build the REST API and microservices |
| Domain model boilerplate | Project Lombok | Reduce repetitive Java domain-class code while keeping state changes explicit |
| Frontend | React | Build the WalletX web application |
| Database | One PostgreSQL database | Persist users, wallets, transfers, transactions, and notifications for the initial solo-developed version |
| Persistence | Spring Data JPA with Hibernate | Java ORM for service-owned data in PostgreSQL |
| Authentication | Spring Security with JWT | Secure REST endpoints and authenticate users |
| API Gateway | Spring Cloud Gateway | Provide a single public entry point and route requests to services |
| Message broker | RabbitMQ | Handle task-oriented communication and notification work |
| Event streaming | Apache Kafka | Publish transfer events for audit, analytics, and independent consumers |
| Real-time notifications | Server-Sent Events (SSE) | Deliver server-side notifications to the React application |
| Containers | Docker and Docker Compose | Run the application and infrastructure locally |
| Load balancer | Nginx | Route traffic and support multiple application instances |
| FaaS | Docker-based function or OpenFaaS | Implement the low-balance notification requirement |
| Documentation | PlantUML and C4 Model | Document architecture and system behavior |

## Architecture Decisions

- WalletX will contain three main backend microservices: User Service, Wallet Service, and Transfer Service.
- The initial version uses one shared PostgreSQL database. Services retain logical ownership of their domain tables and access other domains through APIs or events; separate databases are not required.
- The frontend will be a React shell with modules for authentication, wallets, transfers, and notifications.
- REST APIs will be protected with JWT bearer tokens.
- The User Service requires `JWT_SECRET` to be configured with a signing key of at least 32 bytes; never use a committed or default signing secret.
- Login returns an access token and token type. Do not issue a separate ID token unless WalletX adopts OpenID Connect.
- Java DTOs and other data-holder classes must be regular classes, not Java records. Use Project Lombok for boilerplate accessors and constructors where appropriate; only add setters when mutability is needed, and preserve explicit domain invariants and JPA requirements.
- RabbitMQ will be used for worker-style messaging, while Kafka will be used for event streaming.
- The initial supported currency is RON only.
- Deposits are simulated; no real banking or payment integration is required.
- Docker Compose will be the primary local deployment method.
- Kubernetes, Redis, multiple currencies, and external payment integrations remain optional future extensions.

## Working Conventions

- Prefer clear service boundaries over sharing business logic between services.
- Do not use Java records in Java source; model DTOs and data holders as classes.
- Use Lombok for repetitive getters, setters, and constructors where appropriate rather than hand-writing boilerplate. Avoid unrestricted entity setters and preserve constructors or methods required to enforce domain invariants or JPA behavior.
- Keep authentication and authorization checks on protected backend endpoints.
- Validate that users can access only their own wallets and transactions.
- Keep infrastructure configuration under `infra` and architecture documentation under `docs`.
- If a technology decision changes, update this document before implementing the affected tasks.
