# How to Run — Worst Case Outpost

[Español](./HOW_TO_RUN_ES.md) · [README (EN)](./README.md) · [README (ES)](./README_ES.md)

---

## Table of contents

1. [Requirements](#1-requirements)
2. [Open the project](#2-open-the-project)
3. [Linux / Ubuntu (Desktop)](#3-linux--ubuntu-desktop)
4. [Windows (Desktop)](#4-windows-desktop)
5. [macOS (Desktop)](#5-macos-desktop)
6. [Android](#6-android)
7. [Web (Wasm, optional)](#7-web-wasm-optional)
8. [42 school / low disk space](#8-42-school--low-disk-space)
9. [Common problems](#9-common-problems)

[↑ Back to top](#how-to-run--worst-case-outpost)

---

## 1. Requirements

- **JDK 17+** (Temurin / Oracle / Microsoft Build of OpenJDK)  
- **Git** (optional)  
- For Android: Android Studio + SDK 35 recommended  
- For Desktop: no emulator required  

Check Java:

```bash
java -version
```

[↑ Back to top](#how-to-run--worst-case-outpost)

---

## 2. Open the project

1. Open **Android Studio** or **IntelliJ IDEA**  
2. **File → Open** → select the folder that contains `settings.gradle.kts`  
3. Wait for **Gradle Sync**  

Or from terminal only (Desktop):

```bash
cd path/to/The_Pointers_NASA_2026
./gradlew :composeApp:run
```

You can also use `./Launch.command`: on Linux it uses `JAVA_HOME` or Java from
`PATH`. The desktop icon is bundled from `imgs/icon-nobg.png` and is also used
when building the `.deb` package on Linux.

[↑ Back to top](#how-to-run--worst-case-outpost)

---

## 3. Linux / Ubuntu (Desktop)

```bash
cd path/to/The_Pointers_NASA_2026
chmod +x ./gradlew
./gradlew :composeApp:run
```

If home disk is full (e.g. campus machines):

```bash
export GRADLE_USER_HOME=/path/with/free/space/gradle-home
mkdir -p "$GRADLE_USER_HOME"
./gradlew :composeApp:run
```

[↑ Back to top](#how-to-run--worst-case-outpost)

---

## 4. Windows (Desktop)

Install **JDK 17 or later** and set `JAVA_HOME` to the JDK folder, or make sure
`java` is available in `PATH`. Double-click [Launch.bat](./Launch.bat) in File
Explorer, or open **PowerShell** in the project folder and run:

```powershell
.\Launch.bat
```

`Launch.bat` includes the Gradle wrapper startup logic and launches the desktop
application directly; you do not need to install Gradle separately. The first run
downloads dependencies and needs an Internet connection. Keep the `gradle/`
folder alongside the launcher. If startup fails, the console stays open to show
the error.

Additional Gradle options can be passed after the launcher name, for example
`.\Launch.bat --offline` once dependencies have been downloaded.

(Android Studio’s embedded JDK also works via the Gradle tool window → `composeApp` → `run`.)

[↑ Back to top](#how-to-run--worst-case-outpost)

---

## 5. macOS (Desktop)

Double-click `Launch.command` in Finder, or run `./Launch.command` in Terminal.
The launcher detects Java 17 installed with Homebrew and configures the environment.

If Java is already configured, you can also run:

```bash
cd path/to/The_Pointers_NASA_2026
chmod +x ./gradlew
./gradlew :composeApp:run
```

Apple Silicon: use a JDK 17 build for `aarch64` (Temurin 17).

[↑ Back to top](#how-to-run--worst-case-outpost)

---

## 6. Android

1. Open the project in **Android Studio**  
2. Wait for sync  
3. Select an emulator or device  
4. Run configuration **composeApp** (or `androidApp` if present)  

Command line:

```bash
./gradlew :composeApp:assembleDebug
```

Install the APK from `composeApp/build/outputs/apk/`.

[↑ Back to top](#how-to-run--worst-case-outpost)

---

## 7. Web (Wasm, optional)

Wasm is experimental in this project. If enabled in Gradle:

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

If sync fails on Wasm, comment out `wasmJs { ... }` blocks in `shared/build.gradle.kts` and `composeApp/build.gradle.kts` and use Desktop/Android for the demo.

[↑ Back to top](#how-to-run--worst-case-outpost)

---

## 8. 42 school / low disk space

- Put Gradle cache on **goinfre/sgoinfre**: `export GRADLE_USER_HOME=/goinfre/$USER/gradle-home`  
- Move Android SDK / Studio config off `$HOME` when needed  
- Prefer **Desktop** run for judging demos  

[↑ Back to top](#how-to-run--worst-case-outpost)

---

## 9. Common problems

| Problem | Fix |
|---------|-----|
| `Expected openUrl has no actual` | Ensure `Platform.desktop.kt` is under `desktopMain`, not `commonMain` |
| Smart cast `activeCrisis` | Use a local `val crisis = state.activeCrisis` |
| Blank screen after Junior | Analog / Dashboard screens must not be empty stubs |
| Lessons stay in English | Update language **before** ending the mission, or change language on debrief (lessons rebuild live) |
| No sound | Settings → Mission audio **On** |

[↑ Back to top](#how-to-run--worst-case-outpost)
