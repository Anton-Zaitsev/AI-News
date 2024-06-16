package zaitsev.a.d.mirea.diplom.data

import zaitsev.a.d.mirea.translateml.ml.Language

data class LanguageModelTranslate(
    val language: Language,
    val isExist: StateExistLanguage,
)

enum class StateExistLanguage {
    EXIST,
    NOT_EXIST,
    LOADING
}
