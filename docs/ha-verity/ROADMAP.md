# HA-Verity Roadmap

## 0.1-dev.1 — Foundation

- Establish HA-Verity overlay documentation
- Preserve clean upstream baseline
- Verify Android build
- Verify Wear OS build
- Confirm current upstream voice architecture before modification

## 0.1-dev.2 — Identity

- Introduce HA-Verity branding
- Begin package/application ID migration toward `com.typezero.haverity`
- Add HA-Verity launcher/adaptive icon
- Add HA-Verity version/about presentation

## 0.1-dev.3 — Voice Core

- Reliable microphone capture
- Push-to-talk
- Visible transcription
- Home Assistant Assist pipeline execution
- Stage-aware errors and diagnostics

## 0.1-dev.4 — Android Voice UX

- Tap-to-toggle listening
- Quick Settings voice tile
- Android assistant/default-assistant invocation where supported
- Voice command history/debug view

## 0.1-dev.5 — Wear OS Voice

- Wear push-to-talk
- Direct Home Assistant command execution when connectivity permits
- Haptic confirmation
- Voice-first Wear tile
- Favorites/quick controls

## 0.1-dev.6 — Wake Word

- Optional wake-word mode
- Clear privacy state
- Battery impact controls
- Wake-word diagnostics
- Candidate custom wake phrase: `Hey Verity`

## Later

- Lock-screen invocation
- Additional Wear complications
- Local/on-device STT options
- Pluggable speech backends
- Typezer∅ release/updater standard integration

## Baseline verification

- Android :app:assembleMinimalDebug — PASS
- Wear OS :wear:assembleDebug — PASS
- Verified on Netzach before HA-Verity code divergence
