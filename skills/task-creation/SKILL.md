---
name: task-creation
description: Create a numbered WalletX task Markdown file in changes/todo using the project's task template.
---

# Task Creation

Use this skill whenever a new WalletX development task needs to be recorded.

## Workflow

1. Inspect `changes/todo`, `changes/active`, and `changes/complete` for existing task files named `WalletX-<NUMBER>-<SHORT-DESCRIPTION>.md`.
2. Assign the next numeric ID by taking the highest existing task number and incrementing it. Start at `001` when no tasks exist. Preserve three-digit numbering.
3. Create the new file in `changes/todo` using the filename format `WalletX-<NUMBER>-<SHORT-DESCRIPTION>.md`. Use a concise, lowercase, hyphen-separated short description.
4. Use `changes/task-template.md` as the file structure. Fill in the task details, set the initial status to `Todo`, and leave unknown fields clearly marked for later completion.
5. Do not move, rename, or modify existing tasks as part of task creation.

## Result

Report the created task path and its assigned ID. Task work begins separately; creating a task only records it in `changes/todo`.
