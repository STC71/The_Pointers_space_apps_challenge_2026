# Worst Case Outpost

**NASA Space Apps Challenge 2026** · Entrenador de misiones

[English 🇬🇧](./README_ALTERNATIVE.md) · [Cómo ejecutar (ES)](./HOW_TO_RUN_ES.md) · [How to Run (EN)](./HOW_TO_RUN.md)

---

## Índice

1. [Visión](#1-visión)
2. [Retos NASA](#2-retos-nasa)
3. [Funcionalidades](#3-funcionalidades)
4. [Idiomas](#4-idiomas)
5. [Tripulación y herencia](#5-tripulación-y-herencia)
6. [Ejecutar la aplicación](#6-ejecutar-la-aplicación)
7. [Créditos](#7-créditos)

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
- Imágenes astronómicas de NASA APOD en la introducción y el menú, con un fondo disponible también sin conexión
- Tripulación aleatoria **solo NASA/ESA**, con muchas astronautas, **cargo** y biografía breve. Los cargos y biografías están disponibles en español e inglés; en otros idiomas se muestran en inglés
- Confirmación antes de retirarse de una misión
- Crisis con opciones de respuesta, asesoramiento de Houston y reutilización de hardware histórico
- Sonidos de comunicaciones, alarmas, ambiente del hábitat y música
- Versiones para ordenador y Android, y una opción para navegador

La interfaz incluye planetas, banderas y fichas educativas de energía, agua y
oxígeno. El texto árabe se muestra de derecha a izquierda.

[↑ Volver arriba](#worst-case-outpost)

---

## 4. Idiomas

Inglés, español, francés, portugués, alemán, italiano, japonés, coreano, ucraniano y árabe.

Las **Lecciones aprendidas** del debrief usan el idioma **actual** (puedes cambiarlo en ⚙ Settings).

[↑ Volver arriba](#worst-case-outpost)

---

## 5. Tripulación y herencia

Solo astronautas **NASA y ESA** (p. ej. Sally Ride, Mae Jemison, Peggy Whitson, **Pedro Duque**, Samantha Cristoforetti…). Cada uno tiene **rol en la misión** y una nota histórica breve.

[↑ Volver arriba](#worst-case-outpost)

---

## 6. Ejecutar la aplicación

- **[HOW_TO_RUN_ES.md](./HOW_TO_RUN_ES.md)** — instrucciones detalladas en español
- **[HOW_TO_RUN.md](./HOW_TO_RUN.md)** — instrucciones detalladas en inglés

En macOS, abre [Launch.command](./Launch.command) con doble clic en Finder.
En Linux, ejecuta `./Launch.command` desde Terminal. En Windows, abre
[Launch.bat](./Launch.bat) con doble clic o ejecuta `.\Launch.bat` en PowerShell.
Requiere Java 17 o superior (JDK).

También puedes iniciar la versión de escritorio desde Terminal:

```bash
./gradlew :composeApp:run
```

[↑ Volver arriba](#worst-case-outpost)

---

## 7. Créditos

Proyecto educativo **NASA Space Apps Challenge 2026**.
Equipo **The Pointers** · Worst Case Outpost

[↑ Volver arriba](#worst-case-outpost)
