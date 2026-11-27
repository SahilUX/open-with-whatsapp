# Open in WhatsApp

An Android app (Kotlin, one module) that appears in the "Open with" list for a tapped phone number and opens that number as a WhatsApp chat instead of dialling it. `README.md` has the behaviour, the install steps and a table of example numbers; read it first.

## Layout

- `app/src/main/java/com/sahil/openwithwhatsapp/PhoneNumberNormalizer.kt`: all number rewriting. Pure Kotlin, no Android types, unit tested.
- `RedirectActivity.kt`: invisible activity registered for `tel:` links (`ACTION_VIEW` and `ACTION_DIAL`, not `ACTION_CALL`).
- `WhatsApp.kt`: builds the `wa.me` intent and prefers the WhatsApp app over a browser.
- `Settings.kt`, `SettingsActivity.kt`: default country code (`971`) and the preview field.
- `app/src/test/java/com/sahil/openwithwhatsapp/PhoneNumberNormalizerTest.kt`: the unit tests (JUnit 4).
- `.github/workflows/release.yml`: pushing a `v*` tag builds a signed release APK and attaches it to a GitHub Release. The signing key lives in repository secrets; it is never in the repo.

## Build and test

Needs JDK 17 and the Android SDK (platform 34, build tools 34.0.0). Gradle is 8.7 through the wrapper; the Android Gradle plugin is 8.5.2 and Kotlin 1.9.24. `README.md` lists the Homebrew setup commands.

```sh
export JAVA_HOME=<a JDK 17>
./gradlew test              # PhoneNumberNormalizerTest
./gradlew assembleRelease   # app/build/outputs/apk/release/app-release.apk
```

Without `KEYSTORE_PATH` set, `assembleRelease` signs with the debug key, so a local APK can't update one built by the release workflow.

Not run: nobody has run either command on the machine this file was written on, because it has a JDK 21 and no Android SDK. The commands above come from `README.md` and `app/build.gradle.kts`, and they are unverified here. Whoever runs them first on a machine with the SDK should correct this section.

## Conventions

- Keep the number logic in `PhoneNumberNormalizer.kt`, free of Android classes, so it stays unit testable. A change to how a number is rewritten comes with a test case, and a row in the README's table when it changes what a user sees.
- A number that already has a country code is never changed; the default code is added only when the number is clearly local. Don't weaken that.
- Don't register for `ACTION_CALL` or ask for `CALL_PHONE`.
- `versionCode` and `versionName` in `app/build.gradle.kts` are edited by hand when releasing.
- The project is GPL-3.0; the Gradle wrapper files keep their own Apache-2.0 licence.
- Never commit a keystore, its passwords or `local.properties`.
