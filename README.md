# ISKAM (ale lepší)

Native Android prototype of the ISKAM dorm app for VŠE, built with Jetpack Compose and
Material 3 Expressive. Clickable demo only: all data is hard-coded and nothing talks to a server.

## Getting the APK

- Every push builds an APK in GitHub Actions: open the **Build APK** run and download the `iskam-apk` artifact.
- Pushes to `main` also update the **latest** pre-release under Releases with `iskam.apk`.

The APK is signed with a throwaway debug key, so if Android refuses to install an update, uninstall the old build first.

## Building locally

Needs JDK 17+ and the Android SDK with platform 37.

```bash
./gradlew assembleRelease
```
