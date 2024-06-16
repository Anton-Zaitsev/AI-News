package zaitsev.a.d.mirea.diplom.data

import androidx.compose.runtime.snapshots.SnapshotStateList

data class TranslateModelNews(
    val list: SnapshotStateList<LanguageModelTranslate>,
    val selectedSourceLanguage: LanguageModelTranslate,
    val selectedTargetLanguage: LanguageModelTranslate?
)
