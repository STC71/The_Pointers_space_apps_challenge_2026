# Worst Case Outpost

**NASA Space Apps Challenge 2026** · Mission trainer

[Español 🇪🇸](./README_ALTERNATIVE_ES.md) · [How to Run (EN)](./HOW_TO_RUN.md) · [Cómo ejecutar (ES)](./HOW_TO_RUN_ES.md)

---

## Table of contents

1. [Vision](#1-vision)
2. [NASA challenge tracks](#2-nasa-challenge-tracks)
3. [Features](#3-features)
4. [Languages](#4-languages)
5. [Crew & heritage](#5-crew--heritage)
6. [Run the application](#6-run-the-application)
7. [Credits](#7-credits)

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
- Astronomy images from NASA APOD in the intro and menu, with a background available even without a connection
- **10 languages** for menus, settings, the introduction and lessons
- **Global Settings** on every screen: language + mission audio
- Optional **player name** as Trainee Astronaut in the crew
- Explicit rest/stress effects, design tradeoffs and recharge, science progress and a 0–100 score
- Read-only Houston consequence previews and compact Before / Actual reports
- Random **NASA / ESA** crew, including many women astronauts, with a **role and short biography**. Roles and biographies are available in Spanish and English; other languages show the English text
- Confirmation before withdrawing from a mission
- Crises with response options, Houston advice and opportunities to reuse historical hardware
- Communications sounds, alarms, habitat ambience and music
- Versions for computers and Android, plus a browser option

The interface includes planet artwork, flags and educational panels about energy,
water and oxygen. Arabic text is displayed from right to left.

[↑ Back to top](#worst-case-outpost)

---

## 4. Languages

English, Spanish, French, Portuguese, German, Italian, Japanese, Korean, Ukrainian, Arabic.

Change language anytime from **⚙ Settings**. Lessons on the debrief screen follow the **current** language.

[↑ Back to top](#worst-case-outpost)

---

## 5. Crew & heritage

Crew members are drawn only from **NASA** and **ESA** (e.g. Sally Ride, Mae Jemison, Peggy Whitson, Christina Koch, **Pedro Duque**, Samantha Cristoforetti, Thomas Pesquet…). Each has a **mission role** and a one-line historical note.

[↑ Back to top](#worst-case-outpost)

---

## 6. Run the application

Full platform instructions:

- **[HOW_TO_RUN.md](./HOW_TO_RUN.md)** (English)
- **[HOW_TO_RUN_ES.md](./HOW_TO_RUN_ES.md)** (Spanish)

On macOS, double-click [Launch.command](./Launch.command) in Finder. On Linux,
run `./Launch.command` in Terminal. On Windows, double-click
[Launch.bat](./Launch.bat) or run `.\Launch.bat` in PowerShell.
Requires Java 17 or later (JDK).

You can also start the desktop version from Terminal:

```bash
./gradlew :composeApp:run
```

[↑ Back to top](#worst-case-outpost)

---

## 7. Credits

Educational project for **NASA Space Apps Challenge 2026**.
NASA/ESA names and mission facts are used for **educational storytelling**.

Team: **The Pointers** · Worst Case Outpost

[↑ Back to top](#worst-case-outpost)
