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
8. [Run the project](#8-run-the-project)
9. [Submission notes](#9-submission-notes)
10. [License & credits](#10-license--credits)

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
- Daily NASA APOD background in the intro/menu, with a local fallback and last valid image cache ([details, ES](docs/APOD_BACKGROUND.md))
- **10 languages** (UI + settings + intro + lessons)  
- **Global Settings** on every screen: language + mission audio  
- Optional **player name** as Trainee Astronaut in the crew  
- Explicit rest/stress effects, design tradeoffs and recharge, science progress and a 0–100 score
- Read-only Houston consequence previews and compact Before / Actual reports ([details, ES](docs/CONSEQUENCE_FORECASTS.md))
- Random **NASA / ESA** crew (many women astronauts included) with **role + short bio**  
- Crisis dialog with scroll, AI copilot text, hardware heritage options  
- Synthetic telemetry audio (Quindar, alarms, habitat, short theme motif)  
- Targets: **Desktop (JVM)**, **Android**, optional **Wasm**  

The reconciled version preserves the Experimental mission rules, Houston previews,
APOD and audio lifecycle, together with main's planet artwork, image flags and
energy/water/oxygen educational panels. Arabic, Japanese and Korean use bundled
fonts selected by language; Arabic also uses right-to-left layout.

Validation on 15 November 2026: 61 shared logic tests and 5 desktop UI tests pass;
desktop and Kotlin/Wasm compilation pass, and the web production package builds. Android, real-device audio, and live
APOD/CORS checks remain pending. See [testing and integration notes (ES)](docs/PRUEBAS.md)
and [audio behavior and limitations (ES)](docs/AUDIO_AND_LINKS.md).

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
imgs/            logos
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

## 8. Run the project

Full platform instructions:

- **[HOW_TO_RUN.md](./HOW_TO_RUN.md)** (English)  
- **[HOW_TO_RUN_ES.md](./HOW_TO_RUN_ES.md)** (Spanish)  
- **[Testing guide (ES)](docs/PRUEBAS.md)** — coverage of the 61 logic tests and 5 UI tests and how to run, modify, or extend them.

Quick Desktop test:

```bash
export GRADLE_USER_HOME=/path/to/free/disk/gradle-home   # optional
./gradlew :composeApp:run
```

[↑ Back to top](#worst-case-outpost)

---

## 9. Submission notes (Space Apps)

Suggested demo path for judges (≈3 minutes):

1. Intro slides → Settings language  
2. Enter your name → Junior → pick an analog  
3. Trigger **What is the worst that can happen?** → choose a hardware-reuse option  
4. Finish → debrief score + lessons  

Highlight: multi-language, difficulty, NASA/ESA crew, abandoned hardware narrative.

[↑ Back to top](#worst-case-outpost)

---

## 10. License & credits

Educational project for **NASA Space Apps Challenge 2026**.  
NASA/ESA names and mission facts are used for **educational storytelling**.  
Optional analog photos: prefer [images.nasa.gov](https://images.nasa.gov) and ESA media with attribution (see `composeApp/.../composeResources/drawable/README_IMAGES.md`).

Team: **The Pointers** · Worst Case Outpost  

[↑ Back to top](#worst-case-outpost)
