---
name: start-work
description: Start and manage implementation work for an existing WalletX task through planning, review, branching, and completion.
---

# Start Work on a WalletX Task

Use this skill whenever the user asks to begin working on an existing WalletX task.

## Workflow

1. Find the requested task Markdown file in `changes/todo` using the task number, title, or another identifier supplied by the user. Use the repository's file-search tools; on Unix-like systems this may be done with `ls`/`dir` plus filtering, while on Windows use the equivalent PowerShell search.
2. Confirm the matching task before changing anything. Create `changes/active/<task-file-name-without-extension>/`, then move the task file from `changes/todo` into that folder.
3. Read the task file completely.
4. Create `plan.md` in the task's active folder. Include the proposed implementation, affected project areas, verification steps, and any questions that require the user's answer. Stop and wait for the user's approval or answers before implementation.
5. After the user accepts the plan, use `plan.md` as the implementation guide. Resolve or record any answered questions before proceeding.
6. At the beginning of implementation, create a Git branch named after the task file stem, for example `WalletX-001-create-user-service`.
7. Implement and verify the task. When implementation is ready, stop and let the user review it. Only after the user approves the review may you commit and push the changes to GitHub.
8. Do not close the task after pushing. Wait until the user confirms that the task has been completed and merged. Then move the entire task folder from `changes/active` to `changes/complete`.

## Safety and State Rules

- Never select a task by guess when multiple files match; ask the user to disambiguate.
- Preserve the task Markdown file and `plan.md` inside the task folder throughout active work.
- Do not create the implementation branch before the plan is accepted.
- Do not commit, push, or move the task to `changes/complete` without the corresponding user confirmation.
- Report the task path, branch name, plan status, review status, and final folder transition as work progresses.
