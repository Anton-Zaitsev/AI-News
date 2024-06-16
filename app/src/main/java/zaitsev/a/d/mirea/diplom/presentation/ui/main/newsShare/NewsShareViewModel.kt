package zaitsev.a.d.mirea.diplom.presentation.ui.main.newsShare

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.nl.translate.TranslateLanguage
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import zaitsev.a.d.mirea.diplom.data.LanguageModelTranslate
import zaitsev.a.d.mirea.diplom.data.StateExistLanguage
import zaitsev.a.d.mirea.diplom.data.TranslateModelNews
import zaitsev.a.d.mirea.diplom.data.rss.AstroBeneNews
import zaitsev.a.d.mirea.diplom.data.rss.BBCNews
import zaitsev.a.d.mirea.diplom.data.rss.BankiNews
import zaitsev.a.d.mirea.diplom.data.rss.GoogleNews
import zaitsev.a.d.mirea.diplom.data.rss.MailNews
import zaitsev.a.d.mirea.diplom.data.rss.ModelNews
import zaitsev.a.d.mirea.diplom.data.rss.NewYorkNews
import zaitsev.a.d.mirea.diplom.data.rss.TassNews
import zaitsev.a.d.mirea.diplom.data.rss.classTypeToNews
import zaitsev.a.d.mirea.diplom.db.NewsLocalRepository
import zaitsev.a.d.mirea.diplom.presentation.ui.mainNavigation.Routing
import zaitsev.a.d.mirea.diplom.presentation.updateNewValue
import zaitsev.a.d.mirea.diplom.widget.ZaitsevNewsWidget.Companion.updateWidgetNews
import zaitsev.a.d.mirea.speachtextcompose.AnnotationIndex
import zaitsev.a.d.mirea.speachtextcompose.tts.builder.TextToSpeechHelper
import zaitsev.a.d.mirea.translateml.ml.LanguageML
import zaitsev.a.d.mirea.translateml.ml.LanguageMLRequest
import zaitsev.a.d.mirea.translateml.ml.LanguageRequest
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NewsShareViewModel @Inject constructor(
    @ApplicationContext private val appContext: Lazy<Context>,
    savedStateHandle: SavedStateHandle,
    private val newsLocalRepository: NewsLocalRepository,
    private val translateML: LanguageML,
    private val textToSpeech: TextToSpeechHelper,
    private val snackbarHost: SnackbarHostState
): ViewModel() {

    var isInitTranslateModel by mutableStateOf(true)
        private set
    var mutableNews: ModelNews? by mutableStateOf(null)
        private set

    var indexSpeakTitle by mutableStateOf(AnnotationIndex())
        private set
    var indexSpeakDescription by mutableStateOf(AnnotationIndex())
        private set

    var translate: TranslateModelNews? by mutableStateOf(null)
        private set

    var visibleBMTranslate by mutableStateOf(false)

    val lifecycleEventSpeech get() = textToSpeech.lifecycleEvent
    var hashCodeNews: Int? by mutableStateOf(null)
        private set

    init {
        savedStateHandle.get<String>(Routing.NEWS_ROUTE_TYPE)?.let { type ->
            savedStateHandle.remove<String>(Routing.NEWS_ROUTE_TYPE)
            savedStateHandle.get<String>(Routing.NEWS_ROUTE)?.let { news ->
                val newsUI = type.classTypeToNews(news)
                val hashCode = newsUI.hashCode()
                viewModelScope.launch {
                    mutableNews = newsLocalRepository.getLocalNewsByHashCode(hashCode) ?: newsUI
                }.invokeOnCompletion {
                    hashCodeNews = hashCode
                }
                savedStateHandle.remove<String>(Routing.NEWS_ROUTE)
            }
        }
    }

    fun speakTextNews(){
        mutableNews?.let { news ->
            speakText(message = news.title, setSpeak = { indexTitle ->
                indexSpeakTitle = indexTitle
            }, onDoneSpeech = {
                speakText(message = news.description, setSpeak = { indexDescription ->
                    indexSpeakDescription = indexDescription
                })
            })
        }
    }
    fun getListTranslate() {
        visibleBMTranslate = true
        if (translate != null) return
        mutableNews?.language?.let { language ->
            when(language){
                TranslateLanguage.RUSSIAN -> TranslateLanguage.ENGLISH
                else -> TranslateLanguage.RUSSIAN
            }
        }?.let { languageTarget ->
            viewModelScope.launch {
                val listLanguage = translateML.availableLanguages.map { language ->
                    async {
                        LanguageModelTranslate(
                            language = language,
                            isExist = if (translateML.isExistModel(language))
                                StateExistLanguage.EXIST
                            else StateExistLanguage.NOT_EXIST
                        )
                    }
                }.awaitAll()
                val targetLanguage = listLanguage.firstOrNull{ it.language.code == languageTarget} ?: return@launch
                val sourceLanguage = listLanguage.firstOrNull { it.language.code == requireNotNull(mutableNews?.language) } ?: return@launch
                translate = TranslateModelNews(
                    list = listLanguage.toMutableStateList(),
                    selectedTargetLanguage = targetLanguage,
                    selectedSourceLanguage = sourceLanguage,
                )
            }
        }
    }
    fun onSelectTargetLanguage(language: LanguageModelTranslate){
        translate?.run translateModel@{
            if (language == selectedTargetLanguage) return@translateModel
            val isNotExist = language.isExist == StateExistLanguage.NOT_EXIST
            if (isNotExist){
                this@translateModel.list.updateNewValue(valueOld = language, valueNew = language.copy(isExist = StateExistLanguage.LOADING))
            }
            viewModelScope.launch {
                when(translateML.setClient(
                    languageSource = this@translateModel.selectedSourceLanguage.language.code,
                    languageTarget = language.language.code
                )){
                    is LanguageMLRequest.IsFailure -> {
                        if (isNotExist){
                            this@translateModel.list.updateNewValue(valueOld = language.copy(isExist = StateExistLanguage.LOADING), valueNew = language)
                        }
                        snackbarHost.showSnackbar("Не удалось выбрать данную модель")
                    }
                    LanguageMLRequest.IsSuccessDownload -> {
                        if (isNotExist){
                            this@translateModel.list.updateNewValue(valueOld = language.copy(isExist = StateExistLanguage.LOADING), valueNew = language.copy(isExist = StateExistLanguage.EXIST))
                        }
                        translate = this@translateModel.copy(selectedTargetLanguage = language)
                        visibleBMTranslate = false
                    }
                }
            }
        }
    }
    fun onDeleteLanguage(language: LanguageModelTranslate){
        translate?.run translateModel@{
            if (language == selectedTargetLanguage || language.language.code == TranslateLanguage.ENGLISH) return@translateModel
            this@translateModel.list.updateNewValue(valueOld = language, valueNew = language.copy(isExist = StateExistLanguage.LOADING))
            viewModelScope.launch {
                val result = translateML.deleteLanguage(language = language.language)
                if (result) {
                    this@translateModel.list.updateNewValue(
                        valueOld = language.copy(isExist = StateExistLanguage.LOADING),
                        valueNew = language.copy(isExist = StateExistLanguage.NOT_EXIST)
                    )
                    translate = translate?.copy(selectedTargetLanguage = null)
                } else {
                    this@translateModel.list.updateNewValue(
                        valueOld = language.copy(isExist = StateExistLanguage.LOADING),
                        valueNew = language
                    )
                }
            }
        }
    }

    fun translateNews(){
        if (mutableNews == null) return
        if (translate == null){

            val languageTarget = when(requireNotNull(mutableNews).language){
                TranslateLanguage.RUSSIAN -> TranslateLanguage.ENGLISH
                else -> TranslateLanguage.RUSSIAN
            }

            isInitTranslateModel = false

            textToSpeech.removeSpeak()
            indexSpeakTitle = AnnotationIndex()
            indexSpeakDescription = AnnotationIndex()

            viewModelScope.launch {
                when(translateML.setClient(languageSource = requireNotNull(mutableNews).language, languageTarget = languageTarget)){
                    is LanguageMLRequest.IsFailure -> {
                        isInitTranslateModel = true
                        snackbarHost.showSnackbar("Не удалось перевести данную статью")
                    }
                    LanguageMLRequest.IsSuccessDownload -> {
                        translate()
                    }
                }
            }
            return
        }
        isInitTranslateModel = false

        textToSpeech.removeSpeak()
        indexSpeakTitle = AnnotationIndex()
        indexSpeakDescription = AnnotationIndex()

        viewModelScope.launch {
            translate()
        }
    }

    private suspend fun translate(){
        val title = requireNotNull(mutableNews?.title)
        val description = requireNotNull(mutableNews?.description)

        when(val resultTitle = translateML.translateText(text = title)){
            is LanguageRequest.FailureTranslate -> {
                snackbarHost.showSnackbar("Не удалось сделать перевод новости")
            }
            is LanguageRequest.TranslatedText -> {
                when(val resultDescription = translateML.translateText(description)){
                    is LanguageRequest.FailureTranslate -> {
                        snackbarHost.showSnackbar("Не удалось сделать перевод новости")
                    }
                    is LanguageRequest.TranslatedText -> {
                        mutableNews = when(val result = requireNotNull(mutableNews)){

                            is AstroBeneNews -> {
                                val translateCategory = translateML.translateText(text = result.category.joinToString(separator = "§"))
                                val category = if (translateCategory is LanguageRequest.TranslatedText)
                                    translateCategory.text.split("§").map { it.trim() } else result.category
                                result.copy(title = resultTitle.text, description = resultDescription.text, category = category)
                            }

                            is BankiNews -> {
                                val translateSynopsis = translateML.translateText(text = result.synopsis)
                                val synopsis = if (translateSynopsis is LanguageRequest.TranslatedText)
                                    translateSynopsis.text else result.synopsis

                                val bankName = result.bankName?.let { bank ->
                                    val translateBank = translateML.translateText(text = bank)
                                    if (translateBank is LanguageRequest.TranslatedText) translateBank.text else bank
                                }
                                result.copy(title = resultTitle.text, description = resultDescription.text, synopsis = synopsis, bankName = bankName)
                            }

                            is GoogleNews -> result.copy(title = resultTitle.text, description = resultDescription.text)

                            is MailNews -> {
                                val translateCategory = translateML.translateText(text = result.category)
                                val category = if (translateCategory is LanguageRequest.TranslatedText) translateCategory.text.trim() else result.category
                                result.copy(title = resultTitle.text, description = resultDescription.text, category = category)
                            }

                            is TassNews -> {
                                val translateCategory = translateML.translateText(text = result.category.joinToString(separator = "§"))
                                val category = if (translateCategory is LanguageRequest.TranslatedText)
                                    translateCategory.text.split("§").map { it.trim() } else result.category

                                result.copy(title = resultTitle.text, description = resultDescription.text, category = category)
                            }

                            is BBCNews -> {
                                val translateCategory = translateML.translateText(text = result.category)
                                val category = if (translateCategory is LanguageRequest.TranslatedText) translateCategory.text.trim() else result.category
                                result.copy(title = resultTitle.text, description = resultDescription.text, category = category)
                            }

                            is NewYorkNews -> {
                                val translateCategory = translateML.translateText(text = result.category.joinToString(separator = "§"))
                                val category = if (translateCategory is LanguageRequest.TranslatedText)
                                    translateCategory.text.split("§").map { it.trim() } else result.category

                                result.copy(title = resultTitle.text, description = resultDescription.text, category = category)
                            }
                        }

                        // Переворачиваем языки при удачном переводе
                        val targetLanguage = translate?.selectedTargetLanguage
                        if (targetLanguage != null){
                            val sourceLanguage = requireNotNull(translate?.selectedSourceLanguage)
                            translate = requireNotNull(translate).copy(
                                selectedTargetLanguage = sourceLanguage,
                                selectedSourceLanguage = targetLanguage
                            )
                            translateML.setClient(
                                languageSource = targetLanguage.language.code,
                                languageTarget = sourceLanguage.language.code
                            )
                        }
                        else {
                            val languageSource = when(requireNotNull(mutableNews).language){
                                TranslateLanguage.RUSSIAN -> TranslateLanguage.ENGLISH
                                else -> TranslateLanguage.RUSSIAN
                            }
                            mutableNews = when(val result = requireNotNull(mutableNews)){
                                is AstroBeneNews -> result.copy(language = languageSource)
                                is BankiNews -> result.copy(language = languageSource)
                                is GoogleNews -> result.copy(language = languageSource)
                                is MailNews -> result.copy(language = languageSource)
                                is TassNews -> result.copy(language = languageSource)
                                is BBCNews -> result.copy(language = languageSource)
                                is NewYorkNews -> result.copy(language = languageSource)
                            }
                            translateML.setClient(
                                languageSource = languageSource,
                                languageTarget = requireNotNull(mutableNews).language
                            )
                        }
                    }
                }
            }
        }
        isInitTranslateModel = true
    }
    private fun speakText(message: String, setSpeak: (indexSpeak: AnnotationIndex) -> Unit, onDoneSpeech: (() -> Unit)? = null){

        val languageSpeak = translate?.selectedSourceLanguage?.language?.code ?: requireNotNull(mutableNews).language

        textToSpeech.speakMessage(
            message = message,
            onHighlightListener = setSpeak,
            onDoneListener = {
                setSpeak(AnnotationIndex())
                onDoneSpeech?.invoke()
            },
            onErrorListener = { error ->
                viewModelScope.launch {
                    snackbarHost.showSnackbar("Произошла ошибка при чтении $error")
                }
            },
            language = Locale.forLanguageTag(languageSpeak)
        )
    }

    fun safeLocal(){
        val isLocalSave = mutableNews?.isLocal ?: return
        val hashCode = hashCodeNews ?: return

        viewModelScope.launch {
            val isSafeLocal = if (isLocalSave){
                newsLocalRepository.removeLocalNewsByHashCode(hashCode = hashCode)
                false
            }
            else {
                newsLocalRepository.saveLocal(requireNotNull(mutableNews))
            }

            mutableNews = when(val news = mutableNews){
                is AstroBeneNews -> news.copy(isLocal = isSafeLocal)
                is BBCNews ->  news.copy(isLocal = isSafeLocal)
                is BankiNews ->  news.copy(isLocal = isSafeLocal)
                is GoogleNews ->  news.copy(isLocal = isSafeLocal)
                is MailNews ->  news.copy(isLocal = isSafeLocal)
                is NewYorkNews ->  news.copy(isLocal = isSafeLocal)
                is TassNews ->  news.copy(isLocal = isSafeLocal)
                null -> null
            }
            appContext.get().updateWidgetNews()
        }
    }
}