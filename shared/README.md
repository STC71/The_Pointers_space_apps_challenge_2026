# 🧠 Shared Module

**Location:** `shared/`

This module contains **all the business logic** of the game.  
It is completely independent from Android or Desktop.  
Both platforms use exactly the same code from this module.

---

## 📑 Table of Contents

1. [Why a shared module?](#1-why-a-shared-module)
2. [Folder structure](#2-folder-structure)
3. [Layer responsibilities](#3-layer-responsibilities)
4. [Related documentation](#4-related-documentation)

---

## 1. Why a shared module?

Kotlin Multiplatform allows us to write the game rules **only once**.  
The same code runs on:

- Android phones and tablets
- Desktop computers (Windows, macOS, Linux)
- (Later) Web browsers

This saves a lot of time and avoids bugs that appear when the same logic is written twice.

[↑ Back to top](#-shared-module)

---

## 2. Folder structure

```
shared/src/commonMain/kotlin/com/worstcase/outpost/
├── domain/          ← Pure data models
├── data/            ← Mock data (analogs, crises, crew)
├── presentation/    ← Game state and ViewModel
└── i18n/            ← All translations
```

[↑ Back to top](#-shared-module)

---

## 3. Layer responsibilities

| Layer | Responsibility |
|-------|----------------|
| **domain** | Defines the data structures (what a Crisis is, what Resources are…) |
| **data** | Provides the actual content (the 3 analogs, the 3 crises, etc.) |
| **presentation** | Holds the current state of the game and the functions that change it |
| **i18n** | Stores every text in 10 languages |

[↑ Back to top](#-shared-module)

---

## 4. Related documentation

- [Domain layer](src/commonMain/kotlin/com/worstcase/outpost/domain/README.md)
- [Data layer](src/commonMain/kotlin/com/worstcase/outpost/data/README.md)
- [Presentation layer](src/commonMain/kotlin/com/worstcase/outpost/presentation/README.md)
- [i18n system](src/commonMain/kotlin/com/worstcase/outpost/i18n/README.md)

[↑ Back to top](#-shared-module)

---

**Parent documentation:** [Root README](../README.md)
