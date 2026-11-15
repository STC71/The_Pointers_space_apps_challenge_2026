package com.worstcase.outpost.data

import com.worstcase.outpost.domain.*
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.Strings

/** Read-only training copy recovered from main (a3459a1). No draws or mission effects. */
object TrainingContent {
    // Extended narratives retain main's Spanish/English coverage; labels support all UI languages.
    fun historicalNotes(language: AppLanguage): List<String> {
        val es = language == AppLanguage.SPANISH
        fun t(en: String, spanish: String) = if (es) spanish else en
        return listOf(
            t(
                "Flight note: Apollo 13 showed that non-standard hardware fits can buy survival time when stock spares fail.",
                "Nota de vuelo: Apolo 13 mostró que adaptaciones no estándar pueden ganar tiempo de supervivencia cuando fallan los repuestos."
            ),
            t(
                "Flight note: ISS ECLSS experience stresses early isolation of failing loops before cascading CO₂ or water loss.",
                "Nota de vuelo: la experiencia ECLSS de la ISS insiste en aislar bucles fallidos antes de una cascada de CO₂ o agua."
            ),
            t(
                "Flight note: surface dust events (e.g. Opportunity 2018) remind crews that power and filters are linked risks.",
                "Nota de vuelo: eventos de polvo (p. ej. Opportunity 2018) recuerdan que potencia y filtros son riesgos ligados."
            ),
            t(
                "Flight note: Gemini/Apollo rendezvous discipline still applies — slow down, verify sensors, then commit.",
                "Nota de vuelo: la disciplina de rendezvous Gemini/Apolo sigue válida — reducir ritmo, verificar sensores y luego comprometerse."
            )
        )
    }

    fun houstonContext(
        crisis: Crisis, assessment: AiFlightAssessment, resources: Resources?, language: AppLanguage
    ): List<String> {
        val es = language == AppLanguage.SPANISH
        fun t(en: String, spanish: String) = if (es) spanish else en
        // Reuse the current advisor's selection; never rank options again for the narrative.
        val best = crisis.options.firstOrNull { it.id == assessment.recommendedOptionId }
        val recommendation = when {
            best == null -> t("No viable options encoded.", "No hay opciones codificadas.")
            best.usesAbandonedHardware -> t(
                "Primary call: consider the heritage path only if crew time and tools allow a controlled interface. Otherwise fall back to the next-ranked procedure.",
                "Llamada principal: valorar la vía histórica solo si hay tiempo y herramientas para una interfaz controlada. Si no, usar el siguiente procedimiento."
            )
            resources != null && (resources.crewHealth < 50 || resources.morale < 50) -> t(
                "Primary call: protect crew performance first — fatigue and stress errors dominate this telemetry state.",
                "Llamada principal: proteger primero el rendimiento de tripulación — fatiga y estrés dominan esta telemetría."
            )
            resources != null && (resources.oxygen < 40 || resources.water < 40) -> t(
                "Primary call: stabilise consumables before science or mobility tasks.",
                "Llamada principal: estabilizar consumibles antes de ciencia o movilidad."
            )
            else -> t(
                "Primary call: the highest-scoring procedure balances resource deltas for the current margins.",
                "Llamada principal: el procedimiento con mejor puntuación equilibra deltas de recursos con los márgenes actuales."
            )
        }

        val worstCase = when {
            resources != null && resources.oxygen < 35 -> t(
                "Worst credible case: hypoxia cascade if O₂ is not stabilised within this decision window.",
                "Peor caso creíble: cascada de hipoxia si el O₂ no se estabiliza en esta ventana de decisión."
            )
            resources != null && resources.water < 35 -> t(
                "Worst credible case: water shortfall forces severe rationing and performance loss.",
                "Peor caso creíble: el déficit de agua obliga a racionamiento severo y pérdida de rendimiento."
            )
            resources != null && resources.energy < 30 -> t(
                "Worst credible case: power brownout disables thermal control and communications.",
                "Peor caso creíble: un brownout deja sin control térmico ni comunicaciones."
            )
            else -> t(
                "Worst credible case: delayed action lets a recoverable fault become a multi-system contingency.",
                "Peor caso creíble: retrasar la acción convierte un fallo recuperable en una contingencia multi-sistema."
            )
        }
        val heritage = best?.takeIf { it.usesAbandonedHardware }?.hardwareHistory?.takeIf { it.isNotBlank() }
        return buildList {
            add(recommendation)
            add(worstCase)
            addAll(historicalNotes(language))
            if (heritage != null) add(t("Heritage context: ", "Contexto histórico: ") + heritage)
        }
    }

    fun designRisks(plan: MissionPlan, language: AppLanguage): List<String> {
        fun tr(en: String, es: String): String =
            if (language == AppLanguage.SPANISH) es else en

        // Fixed order — one entry per design axis so the UI never drops a category
        val powerHint = when (plan.power) {
            PowerPlan.SOLAR -> tr(
                "Power (solar): solar storm or dust can cause a sharp energy drop.",
                "Energía (solar): tormenta solar o polvo pueden provocar una caída fuerte de energía."
            )
            PowerPlan.NUCLEAR -> tr(
                "Power (nuclear): thermal / cooling fault on a surface reactor is the main risk.",
                "Energía (nuclear): el riesgo principal es un fallo térmico o de refrigeración del reactor."
            )
            PowerPlan.MIXED -> tr(
                "Power (mixed): grid imbalance under peak loads can cascade into brownouts.",
                "Energía (mixta): el desbalance de la red bajo picos de carga puede causar apagones parciales."
            )
        }

        val habitatHint = when (plan.habitat) {
            HabitatPlan.MINIMAL -> tr(
                "Habitat (minimal): a small leak or CO₂ spike leaves oxygen on the edge.",
                "Hábitat (mínimo): una fuga menor o un pico de CO₂ deja el oxígeno al límite."
            )
            HabitatPlan.STANDARD -> tr(
                "Habitat (standard): ECLSS wear and routine maintenance become critical under stress.",
                "Hábitat (estándar): el desgaste del ECLSS y el mantenimiento rutinario se vuelven críticos bajo estrés."
            )
            HabitatPlan.HARDENED -> tr(
                "Habitat (hardened): more redundancy, but more cascading failure points if systems couple.",
                "Hábitat (reforzado): más redundancia, pero más puntos de fallo en cascada si los sistemas se acoplan."
            )
        }

        val priorityHint = when (plan.priority) {
            PriorityPlan.RESOURCES -> tr(
                "Priority (resources): crew handles long stress less well when wellbeing is secondary.",
                "Prioridad (recursos): la tripulación aguanta peor el estrés prolongado si el bienestar es secundario."
            )
            PriorityPlan.CREW -> tr(
                "Priority (crew): thinner margin if water or power systems fail while focus is on people.",
                "Prioridad (tripulación): menos margen si fallan agua o energía mientras el foco está en las personas."
            )
            PriorityPlan.SCIENCE -> tr(
                "Priority (science): higher power draw and crew fatigue risk from aggressive ops tempo.",
                "Prioridad (ciencia): más consumo energético y riesgo de fatiga por un ritmo operativo agresivo."
            )
        }

        val reuseHint = if (plan.planHardwareReuse) {
            tr(
                "Hardware reuse (ON): heritage parts help, but need time, tools and crew skill under pressure.",
                "Reutilización de hardware (SÍ): el material histórico ayuda, pero hace falta tiempo, herramientas y pericia bajo presión."
            )
        } else {
            tr(
                "Hardware reuse (OFF): fewer recovery options when flown inventory is damaged or depleted.",
                "Reutilización de hardware (NO): menos opciones de recuperación si el inventario embarcado se daña o agota."
            )
        }

        // Always 4 items — independent of language
        return listOf(powerHint, habitatHint, priorityHint, reuseHint)
    }

    fun debriefReflections(resources: Resources, language: AppLanguage): List<String> {
        val texts = Strings.get(language)
        return buildList {
            add(texts.lessonImprovePace)
            add(texts.lessonImproveComm)
            if (resources.water >= 80 || resources.oxygen >= 80) add(texts.lessonImproveStock)
        }
    }
}
