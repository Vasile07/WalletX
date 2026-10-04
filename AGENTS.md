# WalletX Project Instructions

## Task Management

When creating a new task, use the `task-creation` skill located at [skills/task-creation](skills/task-creation). Follow [changes/task-template.md](changes/task-template.md) and create new task files in [changes/todo](changes/todo).

When starting work on an existing task, use the `start-work` skill located at [skills/start-work](skills/start-work). It manages the task's transition from `todo` to `active`, planning, implementation branch, review, and final transition to `complete`.

Task numbers are assigned across [changes/todo](changes/todo), [changes/active](changes/active), and [changes/complete](changes/complete).

## Project Areas

- [docs](docs): project documentation and diagrams
- [my-wallet-be](my-wallet-be): backend implementation
- [my-wallet-fe](my-wallet-fe): frontend implementation
- [infra](infra): infrastructure, deployment, and environment configuration

## Java Conventions

- Do not use Java records. Model DTOs and other data-holder types as regular classes.
- Use Project Lombok for repetitive getters, setters, and constructors where appropriate instead of hand-writing boilerplate.
- Add setters only when mutability is needed. Preserve explicit constructors and methods when required by domain invariants or JPA.
