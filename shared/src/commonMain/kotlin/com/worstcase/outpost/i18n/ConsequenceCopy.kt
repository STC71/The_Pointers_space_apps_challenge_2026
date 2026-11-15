package com.worstcase.outpost.i18n

/** Complete labels for the consequence UI; no fallback to an unrelated language. */
enum class ConsequenceCopy(
    private val en: String, private val es: String, private val fr: String,
    private val pt: String, private val de: String, private val it: String,
    private val ja: String, private val ko: String, private val uk: String, private val ar: String
) {
    COMMON_EFFECTS("All options", "En todas las opciones", "Toutes les options", "Em todas as opções", "Bei allen Optionen", "In tutte le opzioni", "すべての選択肢に共通", "모든 선택지 공통", "Для всіх варіантів", "في جميع الخيارات"),
    ONLY_COMMON("Only the shared effects", "Solo los efectos comunes", "Uniquement les effets communs", "Apenas os efeitos comuns", "Nur die gemeinsamen Effekte", "Solo gli effetti comuni", "共通の影響のみ", "공통 효과만 적용", "Лише спільні наслідки", "الآثار المشتركة فقط"),
    FORECAST_TITLE("Consequence forecast", "Previsión de consecuencias", "Prévision des conséquences", "Previsão de consequências", "Folgenvorschau", "Previsione delle conseguenze", "影響の予測", "결과 예측", "Прогноз наслідків", "توقّع النتائج"),
    FORECAST_NOTE("Includes time and consumption; excludes random events.", "Con tiempo y consumo; sin eventos aleatorios.", "Durée et consommation incluses ; sans événements aléatoires.", "Com tempo e consumo; sem eventos aleatórios.", "Mit Zeit und Verbrauch; ohne Zufallsereignisse.", "Con tempo e consumi; senza eventi casuali.", "時間と消費を含み、ランダムイベントは除きます。", "시간과 소비 포함, 무작위 사건 제외.", "З часом і витратами; без випадкових подій.", "يشمل الوقت والاستهلاك؛ دون أحداث عشوائية."),
    REPORT_TITLE("Actual effects", "Efectos reales", "Effets réels", "Efeitos reais", "Tatsächliche Folgen", "Effetti reali", "実際の影響", "실제 결과", "Фактичні наслідки", "الآثار الفعلية"),
    BEFORE("Before", "Antes", "Avant", "Antes", "Vorher", "Prima", "前", "이전", "До", "قبل"),
    PREDICTED("Forecast", "Previsión", "Prévision", "Previsão", "Vorschau", "Previsione", "予測", "예측", "Прогноз", "التوقع"),
    ACTUAL("Actual", "Real", "Réel", "Real", "Tatsächlich", "Reale", "実際", "실제", "Факт", "الفعلي"),
    DIRECT("Direct decision effect", "Efecto directo de la decisión", "Effet direct de la décision", "Efeito direto da decisão", "Direkte Entscheidungsfolge", "Effetto diretto della decisione", "判断の直接的影響", "결정의 직접 효과", "Прямий ефект рішення", "الأثر المباشر للقرار"),
    PERIOD("Period consumption and recharge", "Consumo y recarga del periodo", "Consommation et recharge de la période", "Consumo e recarga do período", "Verbrauch und Aufladung im Zeitraum", "Consumi e ricarica del periodo", "期間中の消費と充電", "기간 소비와 충전", "Витрати й заряджання за період", "الاستهلاك وإعادة الشحن خلال الفترة"),
    ADDITIONAL("Additional good-news effect", "Efecto adicional de buenas noticias", "Effet supplémentaire des bonnes nouvelles", "Efeito adicional das boas notícias", "Zusätzlicher Effekt guter Nachrichten", "Effetto aggiuntivo delle buone notizie", "朗報による追加効果", "좋은 소식의 추가 효과", "Додатковий ефект добрих новин", "الأثر الإضافي للأخبار الجيدة"),
    BUDGET("Planned use; actual changes respect limits", "Consumo previsto; los cambios reales respetan los límites", "Consommation prévue ; effets réels limités aux réserves", "Consumo previsto; mudanças reais respeitam os limites", "Geplanter Verbrauch; tatsächliche Änderungen beachten Grenzen", "Consumi previsti; variazioni reali entro i limiti", "予定消費量。実際の変化は残量の範囲内です", "예정 소비량이며 실제 변화에는 한도가 적용됩니다", "Планові витрати; фактичні зміни обмежені запасами", "استهلاك مخطط؛ التغييرات الفعلية تراعي الحدود"),
    RECHARGE("Energy recharge", "Recarga de energía", "Recharge d’énergie", "Recarga de energia", "Energieaufladung", "Ricarica energetica", "エネルギー充電", "에너지 충전", "Поповнення енергії", "إعادة شحن الطاقة"),
    UPKEEP("Energy upkeep", "Mantenimiento energético", "Entretien énergétique", "Manutenção energética", "Energieunterhalt", "Consumo di manutenzione", "維持エネルギー", "에너지 유지 비용", "Енергія на обслуговування", "طاقة الصيانة"),
    DAYS("Days elapsed", "Días transcurridos", "Jours écoulés", "Dias decorridos", "Vergangene Tage", "Giorni trascorsi", "経過日数", "경과 일수", "Минуло днів", "الأيام المنقضية"),
    CRITICAL("Critical depletion before additional events: {resources}.", "Agotamiento crítico antes de eventos adicionales: {resources}.", "Épuisement critique avant événements supplémentaires : {resources}.", "Esgotamento crítico antes de eventos adicionais: {resources}.", "Kritische Erschöpfung vor Zusatzereignissen: {resources}.", "Esaurimento critico prima di eventi aggiuntivi: {resources}.", "追加イベント前に枯渇する重要資源：{resources}。", "추가 이벤트 전 핵심 자원 고갈: {resources}.", "Критичне вичерпання до додаткових подій: {resources}.", "نفاد حرج قبل الأحداث الإضافية: {resources}."),
    DETAILS("View breakdown", "Ver desglose", "Voir le détail", "Ver detalhes", "Aufschlüsselung anzeigen", "Vedi dettaglio", "内訳を見る", "세부 정보 보기", "Показати деталі", "عرض التفاصيل"),
    HIDE("Hide breakdown", "Ocultar desglose", "Masquer le détail", "Ocultar detalhes", "Aufschlüsselung ausblenden", "Nascondi dettaglio", "内訳を隠す", "세부 정보 숨기기", "Приховати деталі", "إخفاء التفاصيل"),
    CHOOSE("Choose this option", "Elegir esta opción", "Choisir cette option", "Escolher esta opção", "Diese Option wählen", "Scegli questa opzione", "この選択肢を選ぶ", "이 선택지 선택", "Обрати цей варіант", "اختيار هذا الخيار"),
    REOPEN("Review last decision", "Revisar última decisión", "Revoir la dernière décision", "Rever última decisão", "Letzte Entscheidung ansehen", "Rivedi l’ultima decisione", "直前の判断を確認", "마지막 결정 검토", "Переглянути останнє рішення", "مراجعة القرار الأخير"),
    NOT_CONSULTED("Houston was not consulted. This baseline was saved when the decision was confirmed.", "No se consultó Houston. Este cálculo se guardó al confirmar la decisión.", "Houston n’a pas été consulté. Ce calcul a été enregistré à la confirmation.", "Houston não foi consultado. O cálculo foi salvo ao confirmar a decisão.", "Houston wurde nicht konsultiert. Diese Berechnung wurde bei Bestätigung gespeichert.", "Houston non è stato consultato. Il calcolo è stato salvato alla conferma.", "Houstonへの相談はありませんでした。この計算は判断確定時に保存されました。", "Houston에 문의하지 않았습니다. 이 계산은 결정 확인 시 저장되었습니다.", "До Houston не зверталися. Розрахунок збережено під час підтвердження.", "لم تتم استشارة هيوستن. حُفظ هذا الحساب عند تأكيد القرار."),
    COMPARE_NOTE("Forecast excludes good news; Actual includes it. Changes below are effective after limits.", "La previsión excluye buenas noticias; Real las incluye. Se muestran cambios efectivos tras los límites.", "La prévision exclut les bonnes nouvelles ; le réel les inclut. Les changements respectent les limites.", "A previsão exclui boas notícias; o real as inclui. Mudanças efetivas após os limites.", "Die Vorschau schließt gute Nachrichten aus; Tatsächlich enthält sie. Änderungen nach Begrenzung.", "La previsione esclude le buone notizie; il reale le include. Variazioni effettive dopo i limiti.", "予測には朗報を含まず、実際には含みます。上限・下限適用後の変化です。", "예측에는 좋은 소식이 제외되며 실제에는 포함됩니다. 한도 적용 후 변화입니다.", "Прогноз не враховує добрих новин, факт — враховує. Зміни після застосування меж.", "التوقع يستبعد الأخبار الجيدة والفعلي يشملها. التغييرات معروضة بعد تطبيق الحدود."),
    NO_CHANGE("No change", "Sin cambio", "Aucun changement", "Sem mudança", "Keine Änderung", "Nessuna variazione", "変化なし", "변화 없음", "Без змін", "بلا تغيير"),
    RISE("Rises", "Sube", "Augmente", "Sobe", "Steigt", "Aumenta", "上昇", "증가", "Зростає", "يرتفع"),
    FALL("Falls", "Baja", "Diminue", "Cai", "Sinkt", "Diminuisce", "低下", "감소", "Зменшується", "ينخفض"),
    RISE_SMALL("Small rise", "Subida leve", "Faible hausse", "Subida leve", "Leichter Anstieg", "Lieve aumento", "小幅上昇", "소폭 증가", "Невелике зростання", "ارتفاع طفيف"),
    RISE_MEDIUM("Moderate rise", "Subida moderada", "Hausse modérée", "Subida moderada", "Mäßiger Anstieg", "Aumento moderato", "中程度の上昇", "중간 증가", "Помірне зростання", "ارتفاع متوسط"),
    RISE_LARGE("Large rise", "Subida fuerte", "Forte hausse", "Subida forte", "Starker Anstieg", "Forte aumento", "大幅上昇", "큰 증가", "Значне зростання", "ارتفاع كبير"),
    FALL_SMALL("Small fall", "Bajada leve", "Faible baisse", "Queda leve", "Leichter Rückgang", "Lieve calo", "小幅低下", "소폭 감소", "Невелике зменшення", "انخفاض طفيف"),
    FALL_MEDIUM("Moderate fall", "Bajada moderada", "Baisse modérée", "Queda moderada", "Mäßiger Rückgang", "Calo moderato", "中程度の低下", "중간 감소", "Помірне зменшення", "انخفاض متوسط"),
    FALL_LARGE("Large fall", "Bajada fuerte", "Forte baisse", "Queda forte", "Starker Rückgang", "Forte calo", "大幅低下", "큰 감소", "Значне зменшення", "انخفاض كبير"),
    POINTS("pp", "pp", "pp", "pp", "Prozentpunkte", "pp", "ポイント", "포인트", "в.п.", "نقطة مئوية");

    fun text(language: AppLanguage): String = when (language) {
        AppLanguage.ENGLISH -> en; AppLanguage.SPANISH -> es; AppLanguage.FRENCH -> fr
        AppLanguage.PORTUGUESE -> pt; AppLanguage.GERMAN -> de; AppLanguage.ITALIAN -> it
        AppLanguage.JAPANESE -> ja; AppLanguage.KOREAN -> ko; AppLanguage.UKRAINIAN -> uk; AppLanguage.ARABIC -> ar
    }
}
