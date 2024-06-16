package zaitsev.a.d.mirea.translateml.ml

import com.google.mlkit.nl.translate.TranslateLanguage

internal data class SelectedLanguage(
    @TranslateLanguage.Language val languageSource: String,
    @TranslateLanguage.Language val languageTarget: String
)
