# Worst Case Outpost

**NASA Space Apps Challenge 2026** · Kotlin Multiplatform mission trainer

[Español 🇪🇸](./README_ES.md) · [How to Run (EN)](./HOW_TO_RUN.md) · [Cómo ejecutar (ES)](./HOW_TO_RUN_ES.md)

---

## Table of contents

1. [Vision](#1-vision)
2. [NASA challenge tracks](#2-nasa-challenge-tracks)
3. [Features](#3-features)
4. [Technology](#4-technology)
5. [Project structure](#5-project-structure)
6. [Languages](#6-languages)
7. [Crew & heritage](#7-crew--heritage)
8. [Installation](#8-installation)
9. [Run the project](#9-run-the-project)
10. [Submission notes](#10-submission-notes-space-apps)
11. [License & credits](#11-license--credits)

[↑ Back to top](#worst-case-outpost)

---

## 1. Vision

**Worst Case Outpost** is a simulation / training game: the player manages a habitat at an **Earth analog** of the Moon or Mars and answers the core question:

> **What is the worst that can happen?**

It trains resource management, crew wellbeing, crisis decision-making and the **reuse of abandoned NASA hardware** — with educational debrief lessons after every mission.

[↑ Back to top](#worst-case-outpost)

---

## 2. NASA challenge tracks

| Track | How we address it |
|-------|-------------------|
| Space Mission Design Game | Analog selection → outpost dashboard → crises → debrief |
| Junior Astronaut Mission Trainer | Junior / Mid / Senior difficulty with different penalties |
| Earth analogs (Moon & Mars) | Río Tinto, Lanzarote, Atacama, Haughton… with target sites |
| Abandoned but not Forgotten | Crisis options that recycle historical NASA hardware + storytelling |
| Astronaut health monitoring | Sleep, stress, status + crew health / morale resources |

[↑ Back to top](#worst-case-outpost)

---

## 3. Features

- Intro presentation (3 slides) before the main menu  
- Daily NASA APOD background in the intro/menu, with a local fallback and last valid image cache
- **10 languages** (UI + settings + intro + lessons)  
- **Global Settings** on every screen: language + mission audio  
- Optional **player name** as Trainee Astronaut in the crew  
- Explicit rest/stress effects, design tradeoffs and recharge, science progress and a 0–100 score
- Read-only Houston consequence previews and compact Before / Actual reports
- Random **NASA / ESA** crew (many women astronauts included) with **role + short bio**, refreshed when the language changes (ES/EN catalog; English fallback for other languages)
- Confirmation before withdrawing from a mission
- Crisis dialog with scroll, AI copilot text, hardware heritage options  
- Synthetic telemetry audio (Quindar, alarms, habitat, short theme motif)  
- Targets: **Desktop (JVM)**, **Android**, optional **Wasm**  

The interface includes planet artwork, image flags and educational panels for
energy, water and oxygen. Arabic, Japanese and Korean use bundled fonts selected
by language; Arabic also uses right-to-left layout.

[↑ Back to top](#worst-case-outpost)

---

## 4. Technology

- Kotlin Multiplatform  
- Compose Multiplatform (Material 3, dark NASA theme)  
- Shared module: domain / data / presentation / i18n / audio  
- No backend (local mock data)  

[↑ Back to top](#worst-case-outpost)

---

## 5. Project structure

```text
shared/          business logic, ViewModel, mock data, i18n, audio API
composeApp/      UI + Android / Desktop / Wasm entry points
gradle/          version catalog + wrapper
imgs/            desktop application icon
README.md / README_ES.md
HOW_TO_RUN.md / HOW_TO_RUN_ES.md
```

[↑ Back to top](#worst-case-outpost)

---

## 6. Languages

English, Spanish, French, Portuguese, German, Italian, Japanese, Korean, Ukrainian, Arabic.

Change language anytime from **⚙ Settings**. Lessons on the debrief screen follow the **current** language.

[↑ Back to top](#worst-case-outpost)

---

## 7. Crew & heritage

Crew members are drawn only from **NASA** and **ESA** (e.g. Sally Ride, Mae Jemison, Peggy Whitson, Christina Koch, **Pedro Duque**, Samantha Cristoforetti, Thomas Pesquet…). Each has a **mission role** and a one-line historical note.

[↑ Back to top](#worst-case-outpost)

---

## 8. Installation

The applicacion is available for <br> mobile (Android) </br> and <br>desktop</br> in the [Releases page](https://github.com/STC71/The_Pointers_space_apps_challenge_2026/releases/tag/ReleaseTest)

## 9. Run the project

Full platform instructions:

- **[HOW_TO_RUN.md](./HOW_TO_RUN.md)** (English)  
- **[HOW_TO_RUN_ES.md](./HOW_TO_RUN_ES.md)** (Spanish)  

On macOS, double-click [Launch.command](./Launch.command) in Finder. On Linux,
run `./Launch.command` in Terminal. On Windows, double-click
[Launch.bat](./Launch.bat) or run `.\Launch.bat` in PowerShell.
Requires JDK 17 or later.

Quick Desktop test with Gradle:

```bash
export GRADLE_USER_HOME=/path/to/free/disk/gradle-home   # optional
./gradlew :composeApp:run
```

[↑ Back to top](#worst-case-outpost)

---

## 10. Submission notes (Space Apps)

Suggested demo path for judges (≈3 minutes):

1. Intro slides → Settings language  
2. Enter your name → Junior → pick an analog  
3. Trigger **What is the worst that can happen?** → choose a hardware-reuse option  
4. Finish → debrief score + lessons  

Highlight: multi-language, difficulty, NASA/ESA crew, abandoned hardware narrative.

[↑ Back to top](#worst-case-outpost)

---

## 11. License & credits

Educational project for **NASA Space Apps Challenge 2026**.  
NASA/ESA names and mission facts are used for **educational storytelling**.  
Optional analog photos: prefer [images.nasa.gov](https://images.nasa.gov) and ESA media with attribution.

Team: **The Pointers** · Worst Case Outpost  

[↑ Back to top](#worst-case-outpost)
