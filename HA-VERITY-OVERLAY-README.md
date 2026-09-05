# HA-Verity Branding Overlay — 0.1-dev.2

Drop this overlay into the root of the `home-assistant-android` repository and allow files to merge/overwrite.

This overlay applies:

- Android display name: `HA-Verity`
- Wear OS display name: `HA-Verity`
- HA-Verity Android launcher icon
- HA-Verity Wear launcher icon
- HA-Verity pre-Android-12 full splash artwork
- HA-Verity Android 12+ system splash icon on OLED-black
- OLED-black splash background

It intentionally does NOT touch:

- Kotlin source package names
- `com.typezero.haverity` application-ID work already committed
- Home Assistant entity/Assist behavior
- Voice code
- upstream source namespaces

## After extracting

From the repository root:

```powershell
git status
git diff -- app/src/main/res/values-v31/styles.xml app/src/main/res/drawable/launch_screen_background.xml
```

Then verify builds:

```powershell
.\gradlew.bat :app:assembleMinimalDebug
.\gradlew.bat :wear:assembleDebug
```

If both pass:

```powershell
git add app wear
git commit -m "Apply HA-Verity branding overlay"
```

## Android 12+ note

Android 12 and newer control the initial system splash screen and limit it to a centered icon plus background color. The full-screen generated HA-Verity artwork is used by the legacy launch drawable; Android 12+ uses the HA-Verity emblem on black during the system-owned splash phase.
