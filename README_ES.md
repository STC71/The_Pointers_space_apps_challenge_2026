# Worst Case Outpost

**NASA Space Apps Challenge 2026** · Entrenador de misiones en Kotlin Multiplatform

[English 🇬🇧](./README.md) · [Cómo ejecutar (ES)](./HOW_TO_RUN_ES.md) · [How to Run (EN)](./HOW_TO_RUN.md)

---

## Índice

1. [Visión](#1-visión)
2. [Retos NASA](#2-retos-nasa)
3. [Funcionalidades](#3-funcionalidades)
4. [Tecnología](#4-tecnología)
5. [Estructura](#5-estructura)
6. [Idiomas](#6-idiomas)
7. [Tripulación y herencia](#7-tripulación-y-herencia)
8. [Ejecutar el proyecto](#8-ejecutar-el-proyecto)
9. [Notas para la submission](#9-notas-para-la-submission)
10. [Créditos](#10-créditos)

[↑ Volver arriba](#worst-case-outpost)

---

## 1. Visión

**Worst Case Outpost** es un juego de simulación/entrenamiento: gestionas un hábitat en un **análogo terrestre** de la Luna o Marte y respondes a:

> **¿Qué es lo peor que puede pasar?**

Entrena gestión de recursos, bienestar de tripulación, crisis y **reutilización de hardware abandonado de la NASA**, con lecciones educativas al final de cada misión.

[↑ Volver arriba](#worst-case-outpost)

---

## 2. Retos NASA

| Reto | Cómo lo cubrimos |
|------|------------------|
| Space Mission Design Game | Análogo → dashboard → crisis → debrief |
| Junior Astronaut Mission Trainer | Dificultades Junior / Mid / Senior |
| Análogos terrestres | Río Tinto, Lanzarote, Atacama, Haughton… |
| Abandoned but not Forgotten | Opciones de crisis con hardware histórico |
| Health monitoring | Sueño, estrés, estado + salud/moral |

[↑ Volver arriba](#worst-case-outpost)

---

## 3. Funcionalidades

- Intro de 3 pantallas  
- **10 idiomas** + ajustes globales (idioma y audio)  
- Nombre del jugador como **Tripulante en prácticas**  
- Efectos explícitos de descanso y estrés; diseño con costes y recargas; ciencia y puntuación sobre 100
- Previsiones de consulta en Houston y resumen breve Antes / Real tras cada decisión
- Fondo NASA APOD en introducción y menú, con respaldo local y caché de la última imagen válida
- Tripulación aleatoria **solo NASA/ESA** (con muchas astronautas) + **cargo** y bio, actualizados al cambiar de idioma (catálogo ES/EN; inglés como respaldo para los demás idiomas)
- Confirmación antes de retirarse de una misión
- Crisis con scroll, copiloto IA textual y herencia de hardware  
- Audio sintético (Quindar, alarmas, hábitat, motivo “épico” corto)  
- Desktop, Android y Wasm opcional  

La interfaz incluye planetas, banderas como imágenes y fichas educativas de
energía, agua y oxígeno. Las fuentes incluidas se aplican según el idioma; árabe
también utiliza disposición de derecha a izquierda.

[↑ Volver arriba](#worst-case-outpost)

---

## 4. Tecnología

Kotlin Multiplatform + Compose Multiplatform, arquitectura shared (domain / data / presentation / i18n / audio), sin backend.

[↑ Volver arriba](#worst-case-outpost)

---

## 5. Estructura

```text
shared/          lógica, ViewModel, datos mock, i18n, audio
composeApp/      UI + entradas Android / Desktop / Wasm
README.md / README_ES.md
HOW_TO_RUN.md / HOW_TO_RUN_ES.md
```

[↑ Volver arriba](#worst-case-outpost)

---

## 6. Idiomas

Inglés, español, francés, portugués, alemán, italiano, japonés, coreano, ucraniano y árabe.

Las **Lecciones aprendidas** del debrief usan el idioma **actual** (puedes cambiarlo en ⚙ Settings).

[↑ Volver arriba](#worst-case-outpost)

---

## 7. Tripulación y herencia

Solo astronautas **NASA y ESA** (p. ej. Sally Ride, Mae Jemison, Peggy Whitson, **Pedro Duque**, Samantha Cristoforetti…). Cada uno tiene **rol en la misión** y una nota histórica breve.

[↑ Volver arriba](#worst-case-outpost)

---

## 8. Ejecutar el proyecto

- **[HOW_TO_RUN_ES.md](./HOW_TO_RUN_ES.md)** — instrucciones detalladas en español  
- **[HOW_TO_RUN.md](./HOW_TO_RUN.md)** — same in English  

En macOS, abre [Launch.command](./Launch.command) con doble clic en Finder.
En Linux, ejecuta `./Launch.command` desde Terminal. En Windows, abre
[Launch.bat](./Launch.bat) con doble clic o ejecuta `.\Launch.bat` en PowerShell.
Requiere JDK 17 o superior.

También puedes usar Gradle:

```bash
./gradlew :composeApp:run
```

[↑ Volver arriba](#worst-case-outpost)

---

## 9. Notas para la submission

Demo sugerida (~3 min): intro → idioma → nombre → Junior → análogo → crisis con hardware histórico → debrief.

[↑ Volver arriba](#worst-case-outpost)

---

## 10. Créditos

Proyecto educativo **NASA Space Apps Challenge 2026**.  
Equipo **The Pointers** · Worst Case Outpost  

[↑ Volver arriba](#worst-case-outpost)
