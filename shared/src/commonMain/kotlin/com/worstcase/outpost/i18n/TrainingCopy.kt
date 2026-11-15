package com.worstcase.outpost.i18n

/** Labels for restored learning content; long narratives retain main's ES/EN coverage. */
enum class TrainingCopy(private vararg val translations: String) {
    HOUSTON("History and learning", "Historia y aprendizaje", "Histoire et apprentissage", "História e aprendizagem", "Geschichte und Lernen", "Storia e apprendimento", "歴史と学び", "역사와 학습", "Історія та навчання", "التاريخ والتعلّم"),
    DESIGN("Training scenarios", "Escenarios formativos", "Scénarios pédagogiques", "Cenários de aprendizagem", "Trainingsszenarien", "Scenari formativi", "訓練シナリオ", "훈련 시나리오", "Навчальні сценарії", "سيناريوهات التدريب"),
    REFLECTIONS("Ideas for the next mission", "Ideas para la próxima misión", "Idées pour la prochaine mission", "Ideias para a próxima missão", "Ideen für die nächste Mission", "Idee per la prossima missione", "次のミッションへのヒント", "다음 임무를 위한 제안", "Ідеї для наступної місії", "أفكار للمهمة القادمة"),
    HOUSTON_NOTE(
        "Historical and training context. The current forecast describes the effects of this decision.",
        "Contexto histórico y formativo. La previsión actual describe los efectos de esta decisión.",
        "Contexte historique et pédagogique. La prévision actuelle décrit les effets de cette décision.",
        "Contexto histórico e formativo. A previsão atual descreve os efeitos desta decisão.",
        "Historischer und pädagogischer Kontext. Die aktuelle Vorschau beschreibt die Folgen dieser Entscheidung.",
        "Contesto storico e formativo. La previsione attuale descrive gli effetti di questa decisione.",
        "歴史と訓練の背景です。この判断の影響は現在の予測で確認できます。",
        "역사 및 훈련 배경입니다. 이 결정의 영향은 현재 예측에 표시됩니다.",
        "Історичний і навчальний контекст. Поточний прогноз описує наслідки цього рішення.",
        "سياق تاريخي وتدريبي. يصف التوقّع الحالي آثار هذا القرار."
    ),
    DESIGN_NOTE(
        "Training scenarios. They add no failures or penalties to the rules above.",
        "Escenarios formativos. No añaden averías ni penalizaciones a las reglas anteriores.",
        "Scénarios pédagogiques. Ils n’ajoutent ni pannes ni pénalités aux règles ci-dessus.",
        "Cenários formativos. Não acrescentam avarias nem penalizações às regras acima.",
        "Trainingsszenarien. Sie ergänzen die obigen Regeln um keine Ausfälle oder Strafen.",
        "Scenari formativi. Non aggiungono guasti o penalità alle regole precedenti.",
        "訓練シナリオです。上記のルールに故障やペナルティは追加されません。",
        "훈련 시나리오이며 위 규칙에 고장이나 불이익을 추가하지 않습니다.",
        "Навчальні сценарії. Вони не додають відмов чи штрафів до наведених правил.",
        "سيناريوهات تدريبية لا تضيف أعطالاً أو عقوبات إلى القواعد أعلاه."
    );

    fun text(language: AppLanguage): String = translations[language.ordinal]
}
