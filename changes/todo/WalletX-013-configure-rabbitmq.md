# WalletX-013-configure-rabbitmq

## Task

- **ID:** WalletX-013
- **Title:** Configure RabbitMQ
- **Status:** Todo
- **Priority:** High
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Configure RabbitMQ for task-oriented transfer messaging and worker processing.

## Objective

Provide reliable asynchronous delivery for notification and audit work triggered by completed transfers.

## Scope

### In scope

- Local RabbitMQ configuration, exchanges, queues, routing keys, and retry/dead-letter behavior.
- Transfer message publishing and worker consumption contract.
- Observability for failed messages.

### Out of scope

- Kafka event streaming.
- Final notification UI.

## Acceptance Criteria

- [ ] A completed transfer publishes a routable message.
- [ ] Workers can consume messages with retry and dead-letter behavior.
- [ ] Local setup is reproducible.

## Implementation Tasks

- [ ] Define message contract and broker topology.
- [ ] Add publisher/consumer configuration and integration tests.

## Dependencies

- WalletX-009, WalletX-010.

## Notes

RabbitMQ is for worker-style communication, distinct from Kafka event streaming.
