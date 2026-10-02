# WalletX – Digital Wallet Based on Microservices

## 1. Project Description

WalletX is a simulated digital wallet system based on microservices. Users can create accounts, manage wallets, deposit simulated money, and transfer money to other users.

The initial version supports only one currency: **RON**.

The system is designed to demonstrate secure REST APIs, microservices communication, scalability, event streaming, server-side notifications, containers, and software architecture documentation.

No real banking or payment integration is required.

## 2. Main Features

Users can:

- Register and log in.
- View their profile.
- Create and manage wallets.
- Add simulated money in RON.
- View their wallet balance.
- Transfer money to another user.
- View their transaction history.
- Receive notifications after transfers.

## 3. Microservices

The initial version contains three microservices.

### 3.1 User Service

The User Service manages authentication and user information.

Responsibilities:

- User registration.
- User login.
- Password hashing.
- JWT token generation.
- User profile management.
- Authentication and authorization.

Example endpoints:

```text
POST /auth/register
POST /auth/login
GET  /users/me
```

Example user:

```json
{
  "id": "user-123",
  "name": "John Doe",
  "email": "john@example.com"
}
```

### 3.2 Wallet Service

The Wallet Service manages wallets and balances.

Responsibilities:

- Creating wallets.
- Associating wallets with users.
- Storing the RON balance.
- Adding simulated deposits.
- Returning the current balance.
- Updating the balance after transfers.

Example endpoints:

```text
POST /wallets
GET  /wallets
GET  /wallets/{id}
GET  /wallets/{id}/balance
POST /wallets/{id}/deposit
```

Example wallet:

```json
{
  "id": "wallet-456",
  "ownerId": "user-123",
  "currency": "RON",
  "balance": 1250.50
}
```

Deposits are simulated and exist only for demonstrating the system.

### 3.3 Transfer Service

The Transfer Service manages money transfers between users.

Responsibilities:

- Creating transfers.
- Checking that the sender has sufficient funds.
- Updating sender and receiver balances.
- Storing transfer history.
- Preventing invalid or duplicate transfers.
- Publishing transfer events.
- Triggering notifications.

Example endpoints:

```text
POST /transfers
GET  /transfers
GET  /transfers/{id}
```

Example transfer request:

```json
{
  "senderWalletId": "wallet-111",
  "receiverWalletId": "wallet-222",
  "amount": 100.00,
  "currency": "RON"
}
```

Example transfer event:

```json
{
  "eventType": "MoneyTransferCompleted",
  "transferId": "transfer-123",
  "senderId": "user-111",
  "receiverId": "user-222",
  "amount": 100.00,
  "currency": "RON",
  "timestamp": "2026-10-02T12:00:00Z"
}
```

## 4. System Architecture

```text
                    Web Application
                           |
                           v
                    Nginx Load Balancer
                           |
                           v
                    API Gateway / Web Server
                           |
          +----------------+----------------+
          |                |                |
          v                v                v
    User Service     Wallet Service    Transfer Service
          |                |                |
          +----------------+----------------+
                           |
                    RabbitMQ / Kafka
                           |
                           v
                 Notification Component
```

The web server exposes the public REST API and internally communicates with the three microservices.

## 5. Secured REST API

The REST API is secured using JWT authentication.

The authentication flow is:

```text
1. User sends email and password.
2. User Service validates the credentials.
3. User Service returns a JWT token.
4. The frontend sends the token with future requests.
5. Services validate the token before processing requests.
```

Example request:

```http
Authorization: Bearer <jwt-token>
```

Users can access only their own wallets and transactions.

## 6. Communication Between Services

### RabbitMQ

RabbitMQ can be used for task-oriented communication.

Example:

```text
Transfer Service
      |
      v
RabbitMQ
      |
      +--> Notification Worker
      +--> Audit Worker
```

After a successful transfer, the Transfer Service can send a message to RabbitMQ. The notification worker consumes the message and creates a notification for the sender and receiver.

### Kafka

Kafka can be used for event streaming and transaction history.

Example:

```text
Transfer Service
      |
      v
Kafka topic: money-transfers
      |
      +--> Audit Service
      +--> Analytics Service
      +--> Notification Service
```

Kafka events can be stored and consumed by multiple services independently.

## 7. Server-Side Notifications

The web application should receive notifications when important events happen.

Examples:

- A transfer was received.
- A transfer was sent.
- A transfer failed.
- A deposit was completed.

Notifications can be implemented using:

- WebSockets, or
- Server-Sent Events.

Example notification:

```json
{
  "type": "TRANSFER_RECEIVED",
  "message": "You received 100 RON from John Doe.",
  "createdAt": "2026-10-02T12:00:00Z"
}
```

## 8. Scalability

Nginx can be used as a load balancer in front of multiple instances of the web server.

```text
                    Nginx
                 /    |    \
                /     |     \
        API Instance 1  API Instance 2  API Instance 3
```

This allows the system to handle more requests and provides better availability.

The services can also be scaled independently. For example, if transfers receive many requests, multiple Transfer Service instances can be started without duplicating the other services.

Redis can optionally be used later for caching or scalable WebSocket communication.

## 9. Function as a Service

A FaaS component can be added to demonstrate serverless computing.

A suitable example is a low-balance notification function.

```text
Wallet balance updated
          |
          v
      FaaS Function
          |
          v
If balance < 100 RON, create a low-balance notification
```

The function is executed only when a wallet balance changes. It can check whether the balance is below a predefined threshold and generate a notification.

Possible technologies include:

- OpenFaaS
- AWS Lambda
- Azure Functions
- Google Cloud Functions

For local development, OpenFaaS or a Docker-based function is suitable.

## 10. Web Application

The web application consumes the REST API and displays the wallet functionality.

Main pages:

- Login and registration.
- Dashboard.
- Wallet details.
- Deposit money.
- Transfer money.
- Transaction history.
- Notifications.

Example dashboard information:

```text
Current balance: 1,250.50 RON

Recent transactions:
+ 500.00 RON - Deposit
- 100.00 RON - Transfer to Maria
+ 250.00 RON - Transfer from Alex
```

## 11. Micro Frontend Architecture

The web application can be divided into smaller frontend modules:

```text
Shell Application
├── Authentication Micro Frontend
├── Wallet Micro Frontend
├── Transfers Micro Frontend
└── Notifications Micro Frontend
```

The Shell Application provides navigation and loads the individual micro frontends.

Each micro frontend can be developed independently:

- Authentication handles login and registration.
- Wallet handles balances and deposits.
- Transfers handles sending money and transaction history.
- Notifications displays server-side notifications.

## 12. Containers and Deployment

Each component can run in its own Docker container.

Example containers:

```text
- frontend
- nginx
- user-service
- wallet-service
- transfer-service
- user-database
- wallet-database
- transfer-database
- rabbitmq
- kafka
- redis
```

Docker Compose can be used for local deployment.

Example deployment command:

```text
docker compose up
```

Kubernetes can be considered as an optional extension for orchestration and automatic scaling.

## 13. Suggested Technology Stack

- Frontend: React, Angular, or Vue
- Backend: Java Spring Boot, .NET, or Node.js
- Authentication: JWT
- Databases: PostgreSQL
- Load balancer: Nginx
- Message broker: RabbitMQ
- Event streaming: Kafka
- Notifications: WebSockets or Server-Sent Events
- Cache: Redis
- Containers: Docker and Docker Compose
- FaaS: OpenFaaS
- Documentation: PlantUML and C4 Model

## 14. Documentation

The project documentation should contain the following diagrams.

### C4 Context Diagram

Shows the system, users, and external components.

```text
User --> WalletX System
WalletX System --> RabbitMQ
WalletX System --> Kafka
WalletX System --> Databases
```

### C4 Container Diagram

Shows the web application, API gateway, microservices, brokers, and databases.

### UML Use-Case Diagram

Possible use cases:

- Register.
- Log in.
- Create wallet.
- Deposit money.
- View balance.
- Transfer money.
- View transaction history.
- Receive notification.

### UML Sequence Diagram

A transfer sequence can be represented as:

```text
User
  -> Web Application
  -> Transfer Service
  -> Wallet Service
  -> Database
  -> RabbitMQ/Kafka
  -> Notification Service
  -> Web Application
```

### UML Class Diagram

Possible classes:

- User
- Wallet
- Transfer
- Transaction
- Notification

## 15. Requirement Mapping

| Requirement | Proposed implementation |
|---|---|
| Secured REST API | JWT-authenticated REST API |
| More than two microservices | User, Wallet, and Transfer services |
| Scalability | Nginx load balancer and multiple service instances |
| Message broker | RabbitMQ |
| Event streaming | Kafka |
| FaaS | Low-balance notification function |
| Web application | React, Angular, or Vue application |
| Server-side notifications | WebSockets or Server-Sent Events |
| Micro frontend architecture | Authentication, Wallet, Transfers, and Notifications modules |
| Containers | Docker and Docker Compose |
| Documentation | UML diagrams and C4 models |

## 16. Future Ideas

If there is enough time, the following features can be added:

- Support for EUR and USD.
- Currency conversion.
- Exchange-rate retrieval from an external API.
- Exchange-rate history.
- Multiple wallets per user.
- Virtual cards.
- Card blocking and activation.
- Spending categories.
- Monthly spending statistics.
- Budget limits.
- Scheduled transfers.
- Transfer cancellation.
- Email notifications.
- Two-factor authentication.
- Fraud detection.
- Admin dashboard.
- Audit service.
- Analytics service.
- Kubernetes deployment.
- Mobile application.
- Integration with a real payment provider.