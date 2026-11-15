# Cómo ejecutar — Worst Case Outpost

[English](./HOW_TO_RUN.md) · [README (ES)](./README_ES.md) · [README (EN)](./README.md)

---

## Índice

1. [Requisitos](#1-requisitos)
2. [Abrir el proyecto](#2-abrir-el-proyecto)
3. [Linux / Ubuntu (Desktop)](#3-linux--ubuntu-desktop)
4. [Windows (Desktop)](#4-windows-desktop)
5. [macOS (Desktop)](#5-macos-desktop)
6. [Android](#6-android)
7. [Web (Wasm, opcional)](#7-web-wasm-opcional)
8. [Entorno 42 / poco disco](#8-entorno-42--poco-disco)
9. [Problemas frecuentes](#9-problemas-frecuentes)

[↑ Volver arriba](#cómo-ejecutar--worst-case-outpost)

---

## 1. Requisitos

- **JDK 17+**  
- Android Studio o IntelliJ (recomendado)  
- Para Android: SDK y emulador o móvil  

```bash
java -version
```

[↑ Volver arriba](#cómo-ejecutar--worst-case-outpost)

---

## 2. Abrir el proyecto

**File → Open** → carpeta con `settings.gradle.kts` → esperar **Gradle Sync**.

[↑ Volver arriba](#cómo-ejecutar--worst-case-outpost)

---

## 3. Linux / Ubuntu (Desktop)

```bash
cd ruta/a/The_Pointers_NASA_2026
chmod +x ./gradlew
./gradlew :composeApp:run
```

Si el home está lleno:

```bash
export GRADLE_USER_HOME=/goinfre/$USER/gradle-home
mkdir -p "$GRADLE_USER_HOME"
./gradlew :composeApp:run
```

[↑ Volver arriba](#cómo-ejecutar--worst-case-outpost)

---

## 4. Windows (Desktop)

```powershell
.\gradlew.bat :composeApp:run
```

O desde Android Studio: tarea Gradle `composeApp` → `run`.

[↑ Volver arriba](#cómo-ejecutar--worst-case-outpost)

---

## 5. macOS (Desktop)

Instala Java 17 si todavía no está disponible:

```bash
brew install openjdk@17
```

Abre `Ejecutar.command` con doble clic en Finder, o ejecútalo desde Terminal:

```bash
./Ejecutar.command
```

El lanzador detecta Java 17 instalado con Homebrew (Apple Silicon o Intel) y
configura el entorno para esta ejecución. La primera vez Gradle descarga las
dependencias y necesita conexión a Internet. No hace falta un emulador Android.

Si ya tienes `JAVA_HOME` configurado, también puedes usar:

```bash
chmod +x ./gradlew
./gradlew :composeApp:run
```

Usa JDK 17 nativo (aarch64 en Apple Silicon).

[↑ Volver arriba](#cómo-ejecutar--worst-case-outpost)

---

## 6. Android

Android Studio → dispositivo → Run **composeApp**.

```bash
./gradlew :composeApp:assembleDebug
```

[↑ Volver arriba](#cómo-ejecutar--worst-case-outpost)

---

## 7. Web (Wasm, opcional)

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

Si falla el sync por Wasm, comenta los bloques `wasmJs` y usa Desktop.

[↑ Volver arriba](#cómo-ejecutar--worst-case-outpost)

---

## 8. Entorno 42 / poco disco

Gradle en goinfre, SDK fuera del home, preferir **Desktop** para la demo.

[↑ Volver arriba](#cómo-ejecutar--worst-case-outpost)

---

## 9. Problemas frecuentes

| Problema | Solución |
|----------|----------|
| Pantalla negra tras Junior | Restaurar `AnalogSelectionScreen` / `DashboardScreen` |
| Lecciones solo en inglés | Cambiar idioma en Settings; en debrief se regeneran |
| Sin sonido | Settings → audio On |
| `openUrl` actual missing | `Platform.*.kt` en `desktopMain` / `androidMain`, no en `commonMain` |

[↑ Volver arriba](#cómo-ejecutar--worst-case-outpost)
