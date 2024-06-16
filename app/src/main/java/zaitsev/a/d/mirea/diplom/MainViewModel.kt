package zaitsev.a.d.mirea.diplom

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import zaitsev.a.d.mirea.diplom.data.mappers.rssMappers.toUI
import zaitsev.a.d.mirea.diplom.data.rss.ModelNews
import zaitsev.a.d.mirea.domain.models.use_cases.UseCaseResultList
import zaitsev.a.d.mirea.domain.models.use_cases.astroBeneNews.GetListAtroBeneNews
import zaitsev.a.d.mirea.domain.models.use_cases.bankiNews.GetListBankiNews
import zaitsev.a.d.mirea.domain.models.use_cases.googleNews.GetListGoogleNews
import zaitsev.a.d.mirea.domain.models.use_cases.mailNews.GetListMailNews
import zaitsev.a.d.mirea.domain.models.use_cases.tassNews.GetListTassNews
import zaitsev.a.d.mirea.rsa.RSA
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getListAtroBeneNews: GetListAtroBeneNews,
    private val getListBankiNews: GetListBankiNews,
    private val getListGoogleNews: GetListGoogleNews,
    private val getListMailNews: GetListMailNews,
    private val getListTassNews: GetListTassNews,
    private val snackbarHostState: SnackbarHostState,
    rsa: RSA
): ViewModel() {


    val listAllNews = mutableStateListOf<ModelNews>()
    var loader by mutableStateOf(true)
        private set

    var isError: String? by mutableStateOf(null)
        private set
    init {
        val keyPair1 = rsa.generateKeyPair()
        val keyPair2 = rsa.generateKeyPair()
        val message = "Привет, как у вас дела ?"
        val encryptMessage1 = rsa.encrypt(message, publicKeyUser = keyPair2.publicKey) ?: ""
        val decryptMessage1 = rsa.decrypt(encryptMessage1, myPrivateKey = keyPair2.privateKey)
        Log.d("EncryptMessages", "eencrypt message user1 is ${encryptMessage1}")
        Log.d("EncryptMessages", "decrypt message user1 is ${decryptMessage1}")
        val message2 = "Да все хорошо, у тебя как ?"
        val encryptMessage2 = rsa.encrypt(message2, keyPair1.publicKey) ?: ""
        val decryptMessage2 = rsa.decrypt(encryptMessage2, keyPair1.privateKey)
        Log.d("EncryptMessages", "eencrypt message user2 is ${encryptMessage2}")
        Log.d("EncryptMessages", "decrypt message user2 is ${decryptMessage2}")

        viewModelScope.launch {
            listOf(
                launch {
                    when(val result = getListMailNews.invoke()){
                        is UseCaseResultList.Failure -> isError = result.error
                        is UseCaseResultList.Success -> listAllNews.addAll(result.list.map { it.toUI() })
                    }
                },
                launch {
                    when(val result = getListTassNews.invoke()){
                        is UseCaseResultList.Failure -> isError = result.error
                        is UseCaseResultList.Success -> listAllNews.addAll(result.list.map { it.toUI() })
                    }
                },
                launch {
                    when(val result = getListGoogleNews.invoke()){
                        is UseCaseResultList.Failure -> isError = result.error
                        is UseCaseResultList.Success -> listAllNews.addAll(result.list.map { it.toUI() })
                    }
                },
                launch {
                    when(val result = getListAtroBeneNews.invoke()){
                        is UseCaseResultList.Failure -> isError = result.error
                        is UseCaseResultList.Success -> listAllNews.addAll(result.list.map { it.toUI() })
                    }
                },
                launch {
                    when(val result = getListBankiNews.invoke()){
                        is UseCaseResultList.Failure -> isError = result.error
                        is UseCaseResultList.Success -> listAllNews.addAll(result.list.map { it.toUI() })
                    }
                }
            ).joinAll()
            loader = false
        }.invokeOnCompletion {
            viewModelScope.launch {
                snackbarHostState.showSnackbar("Размер данных ${listAllNews.size}")
            }
        }
    }
}
