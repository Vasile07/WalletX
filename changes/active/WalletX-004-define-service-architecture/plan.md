# Plan: WalletX-004 Define Service Architecture

## Proposed implementation
- Review the existing WalletX product description and technology decisions to confirm the required service domains and communication models.
- Define the service responsibilities, ownership boundaries, and data-model split for user, wallet, transfer, and notification flows.
- Document public REST routes, service-to-service communication, and authentication boundaries for the initial microservice layout.
- Capture the RON-only and simulated-deposit constraints in the architecture baseline and ADR.
- Validate the document against the project requirements and the acceptance criteria.

## Affected project areas
- docs/
- changes/active/WalletX-004-define-service-architecture/

## Verification steps
- Confirm every domain component is mapped to a responsible service and a data owner.
- Check that the document names the public routes and async messaging boundaries.
- Verify the RON-only and simulated-deposit policy is explicit and consistent with the project description.
- Ensure the architecture baseline aligns with the technology-stack decisions and the later implementation tasks.

## Questions / decisions pending
- No blocking questions at this point; proceed with the documented microservice baseline for WalletX.
