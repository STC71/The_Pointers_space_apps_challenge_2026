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

1. Install **JDK 17** and set `JAVA_HOME`  
2. Open **PowerShell** in the project folder:

```powershell
.\gradlew.bat :composeApp:run
```

If `gradlew.bat` is missing, generate the wrapper on another machine or use:

```powershell
gradle :composeApp:run
```

(Android Studio’s embedded JDK also works via the Gradle tool window → `composeApp` → `run`.)

[↑ Back to top](#how-to-run--worst-case-outpost)

---

## 5. macOS (Desktop)

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
