# 🎨 Theme

**Location:** `composeApp/src/commonMain/kotlin/com/worstcase/outpost/theme/`

This folder controls the **visual identity** of the game.

---

## 📑 Table of Contents

1. [Design philosophy](#1-design-philosophy)
2. [Color palette](#2-color-palette)
3. [How to use the theme](#3-how-to-use-the-theme)
4. [Files in this folder](#4-files-in-this-folder)

---

## 1. Design philosophy

The game uses a **dark sci-fi style** inspired by real NASA mission control rooms.

Goals:
- High readability in dark environments
- Clear hierarchy (important actions stand out)
- Orange and red only for warnings and critical moments

[↑ Back to top](#-theme)

---

## 2. Color palette

| Color name     | Hex code   | Usage                              |
|----------------|------------|------------------------------------|
| NasaDarkBlue   | `#0B0F19`  | Main background                    |
| NasaBlue       | `#1B2838`  | Cards and panels                   |
| NasaAccent     | `#00A8E8`  | Primary buttons and highlights     |
| AlertOrange    | `#FF6B35`  | “Worst case” button and warnings   |
| CriticalRed    | `#E63946`  | Danger and critical states         |
| SuccessGreen   | `#2A9D8F`  | Positive values                    |
| TextPrimary    | `#E8EEF4`  | Main text                          |
| TextSecondary  | `#A8B2C1`  | Secondary text                     |

[↑ Back to top](#-theme)

---

## 3. How to use the theme

Wrap the whole application (or each screen) like this:

```kotlin
WorstCaseTheme {
    // All your screens go here
}
```

After that, you can use `MaterialTheme.colorScheme.primary`, etc.

[↑ Back to top](#-theme)

---

## 4. Files in this folder

| File | Purpose |
|------|---------|
| `Theme.kt` | Color definitions + MaterialTheme setup |
| `README.md` | This documentation file |

[↑ Back to top](#-theme)

---

**Parent documentation:** [Root README](../../../../../../README.md)
