# 📊 Data Layer

**Location:** `shared/src/commonMain/kotlin/com/worstcase/outpost/data/`

This folder provides the **fixed content** of the game (analogs, crises, crew).

---

## 📑 Table of Contents

1. [What lives here](#1-what-lives-here)
2. [Why we use mock data](#2-why-we-use-mock-data)
3. [Files in this folder](#3-files-in-this-folder)

---

## 1. What lives here

- The 3 Earth analog locations
- The 3 critical crises of the MVP
- The starting crew members

[↑ Back to top](#-data-layer)

---

## 2. Why we use mock data

In the MVP everything runs offline.  
Later we could replace this file with real NASA open data, but for a 48-hour hackathon mock data is the safest and fastest solution.

[↑ Back to top](#-data-layer)

---

## 3. Files in this folder

| File | Purpose |
|------|---------|
| `MockData.kt` | All fixed content of the game |
| `README.md` | This documentation file |

[↑ Back to top](#-data-layer)

---

**Parent documentation:** [Shared module README](../../../../../../README.md)
