# kami-android

Native Android app for Kami — a free, ad-free, swipe-free dating sanctuary for the sapphic/WLW community.

## Branches and workflow

| Branch | Purpose | Trigger |
|---|---|---|
| `staging` | Active development & integration | Push / PRs to `staging` |
| `main` | Production & release builds | PRs only (always from `staging`) |

Work happens on `staging`. A PR from `staging` into `main` promotes changes to production releases.

## Tech stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Minimum SDK:** API 31 (Android 12)
- **Target SDK:** API 35 (Android 15)
- **Build System:** Gradle with Kotlin DSL and Version Catalogs (`gradle/libs.versions.toml`)

## Building and running locally

### Prerequisites

- [Android Studio Ladybug or newer](https://developer.android.com/studio) / Android SDK
- JDK 17 or newer

### Gradle commands

```sh
# Assemble debug APK
./gradlew assembleDebug

# Run local unit tests
./gradlew test

# Run Android instrumentation tests
./gradlew connectedAndroidTest
```

The assembled debug APK will be located at `app/build/intermediates/apk/debug/app-debug.apk` or `app/build/outputs/apk/debug/app-debug.apk`.
