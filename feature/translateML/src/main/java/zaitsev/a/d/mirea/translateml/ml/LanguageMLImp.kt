package zaitsev.a.d.mirea.translateml.ml

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class LanguageMLImp @Inject constructor(): LanguageML {

    private var translator: Translator? = null

    private var selectedLanguage: SelectedLanguage? = null

    private val modelManager: RemoteModelManager by lazy {
        RemoteModelManager.getInstance()
    }
    override suspend fun setClient(
        @TranslateLanguage.Language languageSource: String,
        @TranslateLanguage.Language languageTarget: String
    ): LanguageMLRequest {

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(languageSource)
            .setTargetLanguage(languageTarget)
            .build()

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator = Translation.getClient(options)
        selectedLanguage = SelectedLanguage(languageSource = languageSource, languageTarget = languageTarget)

        return callbackFlow flowGetModel@{
            requireNotNull(translator).downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    this@flowGetModel.trySend(LanguageMLRequest.IsSuccessDownload)
                }
                .addOnFailureListener { e ->
                    translator = null
                    this@flowGetModel.trySend(
                        LanguageMLRequest.IsFailure(exception = e)
                    )
                }
            awaitClose { cancel() }
        }.firstOrNull() ?: LanguageMLRequest.IsFailure(exception = Exception("Не удалось скачать выбранную модель перевода"))
    }

    private suspend fun translateCurrentText(text: String): LanguageRequest {
        return callbackFlow flowTranslate@{
            requireNotNull(translator).translate(text)
                .addOnSuccessListener { textTranslated ->
                    this@flowTranslate.trySend(LanguageRequest.TranslatedText(text = textTranslated))
                }
                .addOnFailureListener { e ->
                    this@flowTranslate.trySend(
                        LanguageRequest.FailureTranslate(exception = e)
                    )
                }
            awaitClose { cancel() }
        }.firstOrNull() ?: LanguageRequest.FailureTranslate(exception = Exception("Время на перевод вышло"))
    }

    override suspend fun translateText(text: String): LanguageRequest {
        if (translator == null){
            return when(val result = setClient(
                languageSource = selectedLanguage?.languageSource ?: TranslateLanguage.RUSSIAN,
                languageTarget = selectedLanguage?.languageTarget ?: TranslateLanguage.ENGLISH
            )){
                is LanguageMLRequest.IsFailure -> LanguageRequest.FailureTranslate(exception = result.exception)
                LanguageMLRequest.IsSuccessDownload -> {
                    when(val resultTranslate = translateCurrentText(text)){
                        is LanguageRequest.FailureTranslate -> LanguageRequest.FailureTranslate(
                            exception = resultTranslate.exception
                        )
                        is LanguageRequest.TranslatedText -> LanguageRequest.TranslatedText(text = resultTranslate.text)
                    }
                }
            }
        }
        else {
            return when(val resultTranslate = translateCurrentText(text)){
                is LanguageRequest.FailureTranslate -> LanguageRequest.FailureTranslate(exception = resultTranslate.exception)
                is LanguageRequest.TranslatedText -> LanguageRequest.TranslatedText(text = resultTranslate.text)
            }
        }
    }
    override val availableLanguages: List<Language>
        get() = TranslateLanguage.getAllLanguages().map { Language(it) }

    override suspend fun deleteLanguage(language: Language): Boolean {
        return TranslateLanguage.fromLanguageTag(language.code)?.let { languageCode ->
            val model = TranslateRemoteModel.Builder(languageCode).build()

            callbackFlow flowDelete@{
                modelManager.deleteDownloadedModel(model)
                    .addOnSuccessListener {
                        this@flowDelete.trySend(true)
                    }
                    .addOnFailureListener {
                        this@flowDelete.trySend(false)
                    }
                    .addOnCanceledListener {
                        this@flowDelete.trySend(false)
                    }
                awaitClose { cancel() }
            }.firstOrNull() ?: false
        } ?: false
    }

    override suspend fun isExistModel(language: Language): Boolean{
        return TranslateLanguage.fromLanguageTag(language.code)?.let { languageCode ->
            val model = TranslateRemoteModel.Builder(languageCode).build()
            callbackFlow flowCheckExistModel@{
                modelManager.isModelDownloaded(model)
                    .addOnSuccessListener { isDownload ->
                        this@flowCheckExistModel.trySend(isDownload)
                    }
                    .addOnFailureListener {
                        this@flowCheckExistModel.trySend(false)
                    }
                    .addOnCanceledListener {
                        this@flowCheckExistModel.trySend(false)
                    }
                awaitClose { cancel() }
            }.firstOrNull() ?: false
        }?: false
    }
}