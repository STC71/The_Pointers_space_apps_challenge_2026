# 🖥️ UI Layer (Screens)

## Navegación y paneles adaptables

Houston incorpora previsiones breves por opción, mostrando solo cambios y días.
Es un panel de consulta: la decisión se toma desde las opciones de la crisis.
Tras decidir se abre
`DecisionReportDialog`, con comparación Antes / Real y variación efectiva. El
resumen agrupa los cambios de la tripulación. Ninguno de los dos paneles ofrece
«Ver desglose» ni secciones ampliables. Se puede revisar
la última decisión desde dashboard, bienvenida e informe final. El resumen usa el
diálogo desplazable compartido y mantiene Continuar fuera del scroll. Las ayudas
identificadas como Junior se muestran únicamente en Junior.

Destino → inicio, selección de análogo → destino y diseño → selección de análogo
tienen un botón «Volver» traducido a los diez idiomas. En destino y diseño permanece
visible mientras el contenido se desplaza; en la selección queda fuera de la lista.
Android también utiliza la acción Atrás del sistema en estas tres pantallas.

El borrador conserva dificultad, nombre, idioma, sonido y arquitectura al retroceder.
Cambiar Luna por Marte o viceversa descarta el análogo incompatible. La navegación
de preparación no permite salir de una misión ya iniciada y rechaza callbacks de
pantallas anteriores.

Ajustes, información educativa, asesoramiento, ficha del análogo y buenas noticias
usan `ScrollablePanelDialog`: el contenido largo se desplaza dentro de la ventana
y las acciones quedan fuera del scroll. Los diálogos bloquean la pantalla de fondo
y se pueden cerrar mediante la acción de retorno de la plataforma. Las buenas
noticias requieren el botón o la acción de retorno; un clic exterior no las confirma.
La introducción y la bienvenida también admiten desplazamiento del contenido con
sus acciones fuera de la zona desplazable.

La reconciliación del 15 de noviembre de 2026 supera 61 pruebas de lógica y cinco
de interfaz; escritorio y Kotlin/Wasm compilan. Las pruebas de interfaz abren las
fichas de energía/agua/oxígeno, usan banderas PNG, seleccionan un destino ilustrado
y comprueban retroceso, tipografías y dirección árabe. El menú mantiene el diseño
simplificado de main; los idiomas se eligen desde ajustes. Las capturas de destino,
análogos, dashboard y menú se revisaron a 420 × 800 puntos. Quedan pendientes
Android, texto ampliado, Houston con traducciones largas y dispositivos reales.

**Location:** `composeApp/src/commonMain/kotlin/com/worstcase/outpost/ui/`

This folder contains every screen that the player can see.

---

## 📑 Table of Contents

1. [What lives here](#1-what-lives-here)
2. [List of screens](#2-list-of-screens)
3. [How screens talk to the ViewModel](#3-how-screens-talk-to-the-viewmodel)
4. [Files in this folder](#4-files-in-this-folder)

---

## 1. What lives here

Only pure Compose UI code.  
Screens never contain game rules. They only:

- Read the current state from the ViewModel
- Call functions when the player interacts (tap buttons, choose options…)

[↑ Back to top](#️-ui-layer-screens)

---

## 2. List of screens

| Screen | File | Status | Purpose |
|--------|------|--------|---------|
| Home | `HomeScreen.kt` | ✅ Done | APOD background, player name and difficulty selection |
| Analog Selection | `AnalogSelectionScreen.kt` | ✅ Done | Choose Earth analog location |
| Dashboard | `DashboardScreen.kt` | ✅ Done | Main game: resources, crew, crisis button |
| Crisis Dialog | `CrisisDialog.kt` | ✅ Done | Modal that appears during a critical event |
| Debrief | `DebriefScreen.kt` | ✅ Done | Final score and lessons learned |

[↑ Back to top](#️-ui-layer-screens)

---

## 3. How screens talk to the ViewModel

1. The parent composable (`App.kt`) holds the `GameViewModel`.
2. It collects the current `state`.
3. It passes the needed data and callback functions to each screen.
4. The screen stays simple and focused only on layout and interaction.

[↑ Back to top](#️-ui-layer-screens)

---

## 4. Files in this folder

| File | Status |
|------|--------|
| `HomeScreen.kt` | ✅ Implemented |
| `AnalogSelectionScreen.kt` | ✅ Implemented |
| `DashboardScreen.kt` | ✅ Implemented |
| `CrisisDialog.kt` | ✅ Implemented |
| `DebriefScreen.kt` | ✅ Implemented |
| `README.md` | This file |

[↑ Back to top](#️-ui-layer-screens)

---

**Parent documentation:** [Root README](../../../../../../README.md)
