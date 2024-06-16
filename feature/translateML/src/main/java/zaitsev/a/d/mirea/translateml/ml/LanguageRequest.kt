package zaitsev.a.d.mirea.translateml.ml


sealed class LanguageRequest{
    data class TranslatedText(val text: String): LanguageRequest()
    data class FailureTranslate(val exception: Exception): LanguageRequest()
}