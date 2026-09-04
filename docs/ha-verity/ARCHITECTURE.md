# HA-Verity Architecture

## Goal

HA-Verity is a full Home Assistant companion client for Android and Wear OS with
voice control treated as a first-class capability.

It is not a standalone smart-home platform. Home Assistant remains the authority for
entities, services, intents, automations, and Assist pipelines.

## High-level flow

```text
Android / Wear OS microphone
          |
          v
HA-Verity audio capture
          |
          v
Speech / Assist pipeline
          |
          v
Home Assistant
          |
          v
Entities / services / automations
```

## Platform responsibilities

### Android
- Full companion-app role
- Home Assistant frontend/dashboard access
- Authentication and server management
- Notifications
- Sensors and location
- Widgets and Quick Settings
- Voice invocation and diagnostics

### Wear OS
- Voice command entry
- Tiles
- Complications
- Favorites/quick controls
- Notifications
- Haptic response

### Home Assistant
- Entity authority
- Intent resolution
- Automations
- Service execution
- Assist pipeline orchestration

## Voice design principles

1. Manual invocation must work before wake word is attempted.
2. Invocation methods must be independent.
3. Errors must identify the failing stage.
4. Local/Home Assistant-first behavior is the default.
5. No mandatory Google Assistant dependency.
6. Background microphone use must be explicit and visible.
7. Wear OS is a first-class target, not a later add-on.

## Upstream strategy

The official Home Assistant Android project remains the functional foundation.

HA-Verity should isolate custom behavior wherever feasible so upstream merges remain
manageable. Reuse stable upstream components until there is a concrete reason to
replace them.
