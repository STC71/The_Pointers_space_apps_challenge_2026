# 📦 Domain Layer

**Location:** `shared/src/commonMain/kotlin/com/worstcase/outpost/domain/`

This folder contains the pure data models of the game.  
These models do **not** know anything about the user interface or the screen.  
They only describe the information that the game needs.

---

## 📑 Table of Contents

1. [What is the Domain layer?](#1-what-is-the-domain-layer)
2. [Files in this folder](#2-files-in-this-folder)
3. [Main models explained](#3-main-models-explained)
4. [Why we keep models pure](#4-why-we-keep-models-pure)

---

## 1. What is the Domain layer?

In clean architecture the **domain** is the center of the application.

- It contains the most important rules and data structures.
- It does not depend on Android, Desktop, or any UI library.
- Because of this, the same models can be used on Android, Desktop and (later) Web.

[↑ Back to top](#-domain-layer)

---

## 2. Files in this folder

| File | Purpose |
|------|---------|
| `Models.kt` | All data classes and enums used by the game |
| `README.md` | This documentation file |

[↑ Back to top](#-domain-layer)

---

## 3. Main models explained

### Difficulty
Simple choice between Junior (easy) and Mid (medium).

### Analog
Represents a real place on Earth that is similar to the Moon or Mars  
(example: Atacama Desert, Haughton Crater).

### CrewMember
One astronaut. We track sleep, stress and general health.

### Resources
All the supplies of the outpost: energy, water, oxygen, food, crew health and morale.

### Crisis and CrisisOption
A critical event and the possible decisions the player can take.

### GameResult
The final summary shown on the Debrief screen.

[↑ Back to top](#-domain-layer)

---

## 4. Why we keep models pure

Pure models are easy to test and easy to understand.  
They also make it possible to share the same logic between Android and Desktop without any change.

[↑ Back to top](#-domain-layer)

---

**Parent documentation:** [Shared module README](../../../../../../README.md)
