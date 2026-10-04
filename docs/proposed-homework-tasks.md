# Proposed WalletX Homework Tasks

This list is a planning backlog. Individual task files should be created later using the `task-creation` skill and `changes/task-template.md`.

## Foundation

1. **Select and Document Technology Stack** — Record the chosen technologies and architecture decisions.
2. **Initialize Backend Project** — Create the Java Spring Boot workspace and initial service structure.
3. **Initialize Frontend Project** — Create the React shell application and frontend structure.
4. **Define Service Architecture** — Define User, Wallet, Transfer, gateway, databases, and communication boundaries.

## Backend Services

5. **Create User Service** — Implement user registration, profiles, password hashing, and persistence.
6. **Implement Login and JWT Authentication** — Add login, token generation, token validation, and protected endpoints.
7. **Create Wallet Service** — Implement wallet creation, ownership, RON currency, and balance retrieval.
8. **Implement Deposit Money** — Add simulated deposits and wallet balance updates.
9. **Create Transfer Service** — Implement transfers between wallets and persist transfer records.
10. **Implement Transfer Validation** — Validate ownership, balance, currency, amount, and duplicate transfers.
11. **Implement Transaction History** — Expose deposits and transfers for the authenticated user.
12. **Create API Gateway** — Route public API requests to the backend microservices.

## Messaging and Notifications

13. **Configure RabbitMQ** — Publish transfer messages and process them with workers.
14. **Configure Kafka Event Streaming** — Publish transfer events for audit and analytics consumers.
15. **Implement Server-Side Notifications** — Deliver transfer and deposit notifications through SSE.
16. **Implement Low-Balance Function** — Generate a notification when a wallet balance falls below a threshold.

## Frontend

17. **Create Authentication Module** — Build registration, login, logout, and authentication state handling.
18. **Create Wallet Module** — Display wallets, balances, wallet details, and deposit actions.
19. **Create Transfers Module** — Build the transfer form and transfer result views.
20. **Create Notifications Module** — Display real-time notifications from the backend.
21. **Create WalletX Dashboard** — Show current balance, recent transactions, and wallet actions.
22. **Integrate Frontend with Secured APIs** — Connect React modules to the JWT-protected backend.

## Infrastructure

23. **Create Backend Dockerfiles** — Containerize the gateway and backend services.
24. **Create Frontend Dockerfile** — Containerize the React application.
25. **Create Docker Compose Configuration** — Run application services, databases, brokers, and supporting components.
26. **Configure PostgreSQL Databases** — Define persistence and database configuration for the services.
27. **Configure Nginx Load Balancer** — Route traffic and support multiple application instances.

## Documentation and Quality

28. **Create C4 Context Diagram** — Document users, WalletX, databases, brokers, and external components.
29. **Create C4 Container Diagram** — Document the frontend, gateway, services, brokers, and databases.
30. **Create UML Use-Case Diagram** — Document registration, login, wallets, deposits, transfers, and notifications.
31. **Create UML Transfer Sequence Diagram** — Document the end-to-end transfer flow.
32. **Create UML Class Diagram** — Document the main domain classes and relationships.
33. **Add Backend Tests** — Test authentication, authorization, wallets, deposits, and transfers.
34. **Add Frontend Tests** — Test login, dashboard, deposits, transfers, and notifications.
35. **Create Deployment Documentation** — Document setup, environment variables, and Docker Compose usage.
