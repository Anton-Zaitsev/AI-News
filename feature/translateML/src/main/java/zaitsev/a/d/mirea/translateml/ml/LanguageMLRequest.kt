package zaitsev.a.d.mirea.translateml.ml

sealed class LanguageMLRequest {
    data object IsSuccessDownload: LanguageMLRequest()
    data class IsFailure(val exception: Exception): LanguageMLRequest()
}