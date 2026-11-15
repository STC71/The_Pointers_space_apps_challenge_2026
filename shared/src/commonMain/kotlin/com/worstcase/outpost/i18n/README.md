# 🌍 Internationalization (i18n)

**Location:** `shared/src/commonMain/kotlin/com/worstcase/outpost/i18n/`

This folder contains **all the texts** that the player sees on the screen.  
Because every text lives here, we can easily support many languages.

---

## 📑 Table of Contents

1. [Why we use i18n](#1-why-we-use-i18n)
2. [Supported languages](#2-supported-languages)
3. [How the system works](#3-how-the-system-works)
4. [How to add a new language](#4-how-to-add-a-new-language)
5. [Files in this folder](#5-files-in-this-folder)

---

## 1. Why we use i18n

- The game must be usable by people from many countries.
- Writing text directly inside the screens makes translation very hard.
- By putting all texts in one place, we only change this folder when we want a new language.

[↑ Back to top](#-internationalization-i18n)

---

## 2. Supported languages

| Order | Flag | Language | Code |
|-------|------|----------|------|
| 1 | 🇺🇸 | English (default) | `en` |
| 2 | 🇪🇸 | Spanish | `es` |
| 3 | 🇫🇷 | French | `fr` |
| 4 | 🇧🇷 | Portuguese | `pt` |
| 5 | 🇩🇪 | German | `de` |
| 6 | 🇮🇹 | Italian | `it` |
| 7 | 🇯🇵 | Japanese | `ja` |
| 8 | 🇰🇷 | Korean | `ko` |
| 9 | 🇺🇦 | Ukrainian | `uk` |
| 10 | 🇸🇦 | Arabic | `ar` |

The order follows the approximate number of speakers worldwide.

[↑ Back to top](#-internationalization-i18n)

---

## 3. How the system works

1. The player chooses a language (or English is used by default).
2. The code calls `Strings.get(selectedLanguage)`.
3. The function returns a `StringSet` object that contains every text in the chosen language.
4. The screens read the texts from that `StringSet`.

Example:

```kotlin
val texts = Strings.get(AppLanguage.SPANISH)
Text(text = texts.worstCaseButton)   // shows "¿Qué es lo peor que puede pasar?"
```

[↑ Back to top](#-internationalization-i18n)

---

## 4. How to add a new language

1. Add a new value to the `AppLanguage` enum.
2. Create a new `StringSet` with all the translated texts.
3. Add a new `when` branch inside the `Strings.get()` function.

That is all. No other file needs to be changed.

[↑ Back to top](#-internationalization-i18n)

---

## 5. Files in this folder

| File | Purpose |
|------|---------|
| `Strings.kt` | Contains the enum of languages + all translations |
| `README.md` | This documentation file |

[↑ Back to top](#-internationalization-i18n)

---

**Parent documentation:** [Shared module README](../../../../../../README.md)
