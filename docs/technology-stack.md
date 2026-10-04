# WalletX Technology Stack

This document records the current technology decisions for WalletX so future sessions can continue implementation consistently.

## Core Technologies

| Area | Decision | Purpose |
|---|---|---|
| Backend | Java with Spring Boot | Build the REST API and microservices |
| Frontend | React | Build the WalletX web application |
| Databases | PostgreSQL | Persist users, wallets, transfers, transactions, and notifications |
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
- Each service should own its data and database boundaries where practical.
- The frontend will be a React shell with modules for authentication, wallets, transfers, and notifications.
- REST APIs will be protected with JWT bearer tokens.
- RabbitMQ will be used for worker-style messaging, while Kafka will be used for event streaming.
- The initial supported currency is RON only.
- Deposits are simulated; no real banking or payment integration is required.
- Docker Compose will be the primary local deployment method.
- Kubernetes, Redis, multiple currencies, and external payment integrations remain optional future extensions.

## Working Conventions

- Prefer clear service boundaries over sharing business logic between services.
- Keep authentication and authorization checks on protected backend endpoints.
- Validate that users can access only their own wallets and transactions.
- Keep infrastructure configuration under `infra` and architecture documentation under `docs`.
- If a technology decision changes, update this document before implementing the affected tasks.
