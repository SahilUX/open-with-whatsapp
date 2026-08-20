# Open in WhatsApp

An Android app that shows up in the "Open with" list when you tap a phone
number, and opens that number as a WhatsApp chat instead of dialling it.

Tapping `050 123 4567` opens a chat with `+971 50 123 4567`.

## Why

WhatsApp needs numbers in international format. UAE numbers are written locally
as `050 123 4567`, so tapping one anywhere on the phone dials it instead of
opening WhatsApp. This app does the conversion and the hand-off.

## What it does to a number

A number that already has a country code is never changed. The default country
code is only added when the number is clearly local.

| Tapped | Opens |
| --- | --- |
| `050 123 4567` | `wa.me/971501234567` |
| `501234567` | `wa.me/971501234567` |
| `+44 7911 123456` | `wa.me/447911123456` |
| `0044 7911 123456` | `wa.me/447911123456` |
| `+971 (0) 50 123 4567` | `wa.me/971501234567` |
| `+971 4 222 3333,,101` | `wa.me/97142223333` |
| `999`, `*100#` | Nothing — shows an error |

The country code defaults to `971` and can be changed on the app's main screen,
which also has a preview box for checking a number before you trust it.

## Install

Grab `app-release.apk` from the build output and install it, or:

```sh
adb install -r app/build/outputs/apk/release/app-release.apk
```

Then tap any phone number and pick **Open in WhatsApp**.

## Build

Needs JDK 17 and the Android SDK.

```sh
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
./gradlew test              # normalizer unit tests
./gradlew assembleRelease   # APK, signed with the debug key
```

On a fresh machine:

```sh
brew install openjdk@17
brew install --cask android-commandlinetools
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
echo "sdk.dir=/opt/homebrew/share/android-commandlinetools" > local.properties
```

## Notes

- Registers for `ACTION_VIEW` and `ACTION_DIAL` on `tel:`, but deliberately not
  `ACTION_CALL` — that would need the `CALL_PHONE` permission and make the app
  look like a dialer replacement.
- Pick "Just once" in the chooser, not "Always", or every phone number tap goes
  to WhatsApp and you can't call anyone. Undo via Settings → Apps → Default apps.
- If you have a cloned WhatsApp, Android will ask which copy to use. Both clones
  share the package name `com.whatsapp` and only differ by user profile, so no
  app can make that choice for you.

## Layout

| File | Role |
| --- | --- |
| `PhoneNumberNormalizer.kt` | All the number rewriting. Pure Kotlin, unit tested. |
| `RedirectActivity.kt` | Invisible activity registered for `tel:` links. |
| `WhatsApp.kt` | Builds the `wa.me` intent, prefers the app over a browser. |
| `SettingsActivity.kt` | Country code and the preview field. |
