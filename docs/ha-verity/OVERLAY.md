# HA-Verity Overlay

HA-Verity is a private Android + Wear OS Home Assistant companion client built from
the official Home Assistant Android codebase.

## Canonical identity

- Project/repository name: `HA-Verity`
- Android application/package ID: `com.typezero.haverity`
- Current development version: `0.1-dev.1`
- Upstream: `home-assistant/android`

## Overlay philosophy

Preserve upstream Home Assistant structure and behavior whenever practical.

Prefer HA-Verity-specific resources, wrappers, services, modules, and documentation
over invasive rewrites. Modify upstream implementation only when HA-Verity requires it.

Every intentional divergence from upstream should be documented here or in a linked
design note so future upstream merges remain understandable.

## Initial overlay areas

### Branding
- HA-Verity app name
- HA-Verity launcher/adaptive icon
- Splash/launch identity
- HA-Verity About/version presentation

### Voice
- Reliable push-to-talk
- Tap-to-toggle listening
- Android assistant/default-assistant invocation where supported
- Quick Settings voice tile
- Explicit pipeline-stage diagnostics
- Optional wake-word mode
- Local/Home Assistant-first voice path

### Wear OS
- HA-Verity Wear identity
- Push-to-talk voice commands
- Voice-first tile
- Favorites and quick controls
- Haptic success/failure feedback
- Optional wake-word work only after reliable manual invocation

## Voice reliability rule

Voice must be more reliable than clever.

Each invocation path should remain independently usable:

1. Typed command
2. Push-to-talk
3. Tap-to-toggle listening
4. Android assistant/gesture invocation
5. Quick Settings tile
6. Wear OS push-to-talk
7. Optional wake word

Failure in one layer must not make the entire voice feature unusable.

## Diagnostics rule

HA-Verity must not reduce voice failures to a generic error.

Distinguish at minimum:

- Microphone unavailable
- Audio capture failure
- Speech recognition failure
- Home Assistant unreachable
- Assist pipeline unavailable
- Intent/entity resolution failure
- Command understood but action failed
- Response/TTS failure

## Upstream divergence log

Add entries here as implementation begins.

| Area | Upstream path | HA-Verity change | Reason |
| --- | --- | --- | --- |
| Foundation | — | Overlay documentation only | Establish project rules before code changes |
