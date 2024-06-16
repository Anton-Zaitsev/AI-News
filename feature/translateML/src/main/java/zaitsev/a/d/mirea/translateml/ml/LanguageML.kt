package zaitsev.a.d.mirea.translateml.ml

import com.google.mlkit.nl.translate.TranslateLanguage

interface LanguageML {

    suspend fun setClient(
        @TranslateLanguage.Language languageSource: String = TranslateLanguage.RUSSIAN,
        @TranslateLanguage.Language languageTarget: String = TranslateLanguage.ENGLISH,
    ): LanguageMLRequest

    suspend fun translateText(text: String): LanguageRequest

    val availableLanguages: List<Language>
    suspend fun deleteLanguage(language: Language): Boolean
    suspend fun isExistModel(language: Language): Boolean
}