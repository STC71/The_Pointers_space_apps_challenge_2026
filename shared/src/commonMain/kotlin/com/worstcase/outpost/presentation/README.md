# 🧠 Presentation Layer

**Location:** `shared/src/commonMain/kotlin/com/worstcase/outpost/presentation/`

This folder contains the **game brain**: the current state of the game and the functions that change that state.

---

## 📑 Table of Contents

1. [What is the Presentation layer?](#1-what-is-the-presentation-layer)
2. [Files in this folder](#2-files-in-this-folder)
3. [How the ViewModel works](#3-how-the-viewmodel-works)
4. [Main functions explained](#4-main-functions-explained)

---

## 1. What is the Presentation layer?

In clean architecture the presentation layer sits between the pure domain models and the user interface.

- It holds the **current state** of the game (resources, crew, active crisis, which screen is visible…).
- It exposes **functions** that the screens can call (select difficulty, trigger crisis, resolve crisis…).
- The screens never change data directly. They only call these functions.

[↑ Back to top](#-presentation-layer)

---

## 2. Files in this folder

| File | Purpose |
|------|---------|
| `GameViewModel.kt` | Holds the game state + all the functions that change it |
| `README.md` | This documentation file |

[↑ Back to top](#-presentation-layer)

---

## 3. How the ViewModel works

1. The ViewModel creates a private `MutableStateFlow` that contains a `GameUiState`.
2. Screens collect the public `state` (read-only).
3. When the player does something, the screen calls a function (`selectDifficulty`, `triggerCrisis`…).
4. The function updates the state.
5. Because the state is a Flow, the UI automatically re-draws with the new values.

[↑ Back to top](#-presentation-layer)

---

## 4. Main functions explained

| Function | What it does |
|----------|--------------|
| `setLanguage` | Changes the current language of the app |
| `selectDifficulty` | Saves difficulty and moves to Destination |
| `selectAnalog` | Moves to Mission Design |
| `confirmMissionDesign` | Initializes crew and derived resources, then starts training |
| `triggerCrisis` | Picks a remaining crisis or ends the mission |
| `resolveCrisis` | Validates crisis id, occurrence and option id, then commits one turn |
| `requestEndMission` | Requests early withdrawal; the engine determines the outcome |
| `restart` | Resets everything for a new game |

[↑ Back to top](#-presentation-layer)

---

**Parent documentation:** [Shared module README](../../../../../../README.md)

## Mission invariants

Houston now saves `DecisionForecast` snapshots for the active occurrence. Resolution
uses the matching saved snapshot, then records a `DecisionReport` containing the
post-reward state. `showDecisionReport` blocks the next decision and good-news
acknowledgment; closing it only affects presentation. Terminal results still use
the definitive snapshot and the report remains available on the next screen.
Forecasts include capped direct resources and a separate period budget. Shared
critical thresholds serve both forecasts and mission-end evaluation. See
`docs/CONSEQUENCE_FORECASTS.md` at the repository root for precision and UI behavior.

Decision consequences now live in `domain/MissionRules.kt`. The turn coordinator
and the local advisor use the same pure calculation, including difficulty,
trainee costs, caps, time, life-support use, recharge and science gain. Good-news
rewards remain a separate, single application after that calculation.

There is no resource-sign counting or hidden good/bad-choice recovery. Sleep,
stress and health changes belong to each option. Routine work defaults to -8
sleep and +3 stress; only harmful changes scale with difficulty and trainee status.
Rest and stress relief do not receive those penalty multipliers.

The design screen describes the actual initial and recurring rules. Science earns
2 points per crisis normally and 6 with science priority, adjusted by the chosen
action. It contributes 15% of a normalized 0–100 score; survival margins contribute
85%. Science is optional for mission completion. See `docs/MISSION_BALANCE.md`
at the repository root for costs and remaining validation.

- `CrewMember.status` is the health source of truth. `aggregateCrewHealth` uses
  integer division (mean rounded down); an empty crew has health zero and cannot
  start or advance a mission. Sleep and stress remain separate indicators, with
  the existing explicit crisis effects and trainee modifiers; there is no extra
  health penalty derived from either indicator.
- Initial health uses difficulty bases 98/88/80, individual variation, one CREW
  priority bonus of +5 and one HARDENED habitat bonus of +4, capped at 100.
- Each turn validates the decision, applies effects, advances time and consumes
  supplies, selects and applies one eligible good-news reward, recalculates health,
  and evaluates the outcome on that definitive snapshot. Rewards can rescue a
  reserve that reached zero earlier in the same turn.
- Good-news acknowledgment only closes the message and presents any terminal
  result. While it is open, no crisis or withdrawal can intervene. There are no
  pre-reward pending success flags.
- Success requires all target crises resolved and health, oxygen, water and food
  strictly positive, with no unresolved crisis. Depletion stores the actual critical
  reserves in `MissionEndReason`; early withdrawal has its own cause. Energy and
  morale are not separate defeat conditions.
- Occurrence ids survive restart to reject stale callbacks, even for reused catalog
  ids. Restart clears mission state and preserves language, player name and sound.

The related tests were updated but not executed for this change. Compilation,
application execution and manual checks remain pending; static review does not
establish runtime correctness.
