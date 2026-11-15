package com.worstcase.outpost.i18n

enum class ApodCopy(
    private val en: String, private val es: String, private val fr: String,
    private val pt: String, private val de: String, private val it: String,
    private val ja: String, private val ko: String, private val uk: String, private val ar: String
) {
    DETAILS("Background details", "Detalles del fondo", "Détails du fond", "Detalhes do fundo", "Hintergrunddetails", "Dettagli dello sfondo", "背景の詳細", "배경 정보", "Відомості про фон", "تفاصيل الخلفية"),
    CREDITS("Image credits", "Créditos de la imagen", "Crédits de l’image", "Créditos da imagem", "Bildnachweis", "Crediti dell’immagine", "画像クレジット", "이미지 출처", "Авторство зображення", "مصدر الصورة"),
    COPYRIGHT("Copyright", "Derechos de autor", "Droits d’auteur", "Direitos autorais", "Urheberrecht", "Diritti d’autore", "著作権", "저작권", "Авторські права", "حقوق النشر"),
    SOURCE("Open NASA APOD", "Abrir NASA APOD", "Ouvrir NASA APOD", "Abrir NASA APOD", "NASA APOD öffnen", "Apri NASA APOD", "NASA APODを開く", "NASA APOD 열기", "Відкрити NASA APOD", "فتح NASA APOD");

    fun text(language: AppLanguage): String = when (language) {
        AppLanguage.ENGLISH -> en; AppLanguage.SPANISH -> es; AppLanguage.FRENCH -> fr
        AppLanguage.PORTUGUESE -> pt; AppLanguage.GERMAN -> de; AppLanguage.ITALIAN -> it
        AppLanguage.JAPANESE -> ja; AppLanguage.KOREAN -> ko; AppLanguage.UKRAINIAN -> uk; AppLanguage.ARABIC -> ar
    }
}
