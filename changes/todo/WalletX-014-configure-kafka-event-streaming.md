# WalletX-014-configure-kafka-event-streaming

## Task

- **ID:** WalletX-014
- **Title:** Configure Kafka Event Streaming
- **Status:** Todo
- **Priority:** High
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Publish transfer events to Kafka for independent audit and analytics consumers.

## Objective

Establish an event-streaming contract that supports multiple consumers without coupling them to the Transfer Service.

## Scope

### In scope

- `money-transfers` topic and event schema.
- Producer configuration, serialization, keys, and delivery settings.
- Local broker setup and a basic consumer verification path.

### Out of scope

- Production analytics implementation.
- Replacing transactional transfer persistence.

## Acceptance Criteria

- [ ] Completed transfers publish the documented event fields.
- [ ] Consumers can independently read and process events.
- [ ] Event failures are observable and do not silently lose data.

## Implementation Tasks

- [ ] Define versioned event contract and topic configuration.
- [ ] Implement producer and a verification consumer.

## Dependencies

- WalletX-009, WalletX-010.

## Notes

Use Kafka for event streaming and retain RabbitMQ for task-oriented workers.
