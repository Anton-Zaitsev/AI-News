package zaitsev.a.d.mirea.diplom.presentation.ui.main.newsToday

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import zaitsev.a.d.mirea.diplom.constApp.Constants
import zaitsev.a.d.mirea.diplom.data.mappers.rssMappers.toUI
import zaitsev.a.d.mirea.diplom.data.rss.AstroBeneNews
import zaitsev.a.d.mirea.diplom.data.rss.BBCNews
import zaitsev.a.d.mirea.diplom.data.rss.BankiNews
import zaitsev.a.d.mirea.diplom.data.rss.GoogleNews
import zaitsev.a.d.mirea.diplom.data.rss.MailNews
import zaitsev.a.d.mirea.diplom.data.rss.ModelNews
import zaitsev.a.d.mirea.diplom.data.rss.NewYorkNews
import zaitsev.a.d.mirea.diplom.data.rss.TassNews
import zaitsev.a.d.mirea.diplom.db.NewsLocalRepository
import zaitsev.a.d.mirea.diplom.presentation.updateNewValue
import zaitsev.a.d.mirea.domain.models.newsTypes.BBCType
import zaitsev.a.d.mirea.domain.models.newsTypes.NewYorkType
import zaitsev.a.d.mirea.domain.models.use_cases.UseCaseResultList
import zaitsev.a.d.mirea.domain.models.use_cases.astroBeneNews.GetListAtroBeneNews
import zaitsev.a.d.mirea.domain.models.use_cases.bankiNews.GetListBankiNews
import zaitsev.a.d.mirea.domain.models.use_cases.bbcNews.GetListBBCNews
import zaitsev.a.d.mirea.domain.models.use_cases.googleNews.GetListGoogleNews
import zaitsev.a.d.mirea.domain.models.use_cases.mailNews.GetListMailNews
import zaitsev.a.d.mirea.domain.models.use_cases.newYorkNews.GetListNewYorkNews
import zaitsev.a.d.mirea.domain.models.use_cases.tassNews.GetListTassNews
import javax.inject.Inject
import kotlin.reflect.KClass

@HiltViewModel
class NewTodayViewModel @Inject constructor(
    private val newsLocalRepository: NewsLocalRepository,
    private val getListAtroBeneNews: GetListAtroBeneNews,
    private val getListBankiNews: GetListBankiNews,
    private val getListGoogleNews: GetListGoogleNews,
    private val getListMailNews: GetListMailNews,
    private val getListTassNews: GetListTassNews,
    private val getListBBCNews: GetListBBCNews,
    private val getListNewYorkNews: GetListNewYorkNews,
    private val snackbarHostState: SnackbarHostState,
): ViewModel() {

    val listChip = Constants.getListAllNewsRSS()
    var savedNews by mutableIntStateOf(0)
        private set

    private var jobLoadedNews: Job? = null
    var news: ModelNewsWithLoading by mutableStateOf(ModelNewsWithLoading())
        private set
    var selectedBBC: BBCType? by mutableStateOf(null)
        private set

    var selectedNY: NewYorkType? by mutableStateOf(null)
        private set

    init {
        viewModelScope.launch {
            loadNews(listChip.first { it.selected }.type)
        }
        viewModelScope.launch {
            newsLocalRepository.getFlowCountSavedNews()?.collect { countSavedNews ->
                savedNews = countSavedNews
            }
        }
    }

    fun onClickClip(index: Int){
        listChip.getOrNull(index = index)?.let { item ->
            if (!item.selected || item.type == BBCNews::class || item.type == NewYorkNews::class){
                jobLoadedNews?.cancel()

                listChip.filter { it.selected }.forEach { itemSelected ->
                    listChip.updateNewValue(valueOld = itemSelected, valueNew = itemSelected.copy(selected = false))
                }
                listChip.updateNewValue(index, item.copy(selected = true))
                jobLoadedNews = viewModelScope.launch {
                    loadNews(type = item.type)
                }
            }
        }
    }

    private suspend fun loadNews(type: KClass<out ModelNews>){
        news = ModelNewsWithLoading()
        when(type){
            AstroBeneNews::class -> when(val result = getListAtroBeneNews.invoke()){
                is UseCaseResultList.Failure -> {
                    news = ModelNewsWithLoading(isLoaded = true)
                    snackbarHostState.showSnackbar(result.error)
                }
                is UseCaseResultList.Success -> {
                    news = ModelNewsWithLoading(isLoaded = true, listNews = result.list.map { atroBeneNewsDTO -> atroBeneNewsDTO.toUI() })
                }
            }
            BankiNews::class -> when(val result = getListBankiNews.invoke()){
                is UseCaseResultList.Failure -> {
                    news = ModelNewsWithLoading(isLoaded = true)
                    snackbarHostState.showSnackbar(result.error)
                }
                is UseCaseResultList.Success -> {
                    news = ModelNewsWithLoading(isLoaded = true, listNews = result.list.map { bankiNewsDTO -> bankiNewsDTO.toUI() })
                }
            }
            GoogleNews::class -> when(val result = getListGoogleNews.invoke()){
                is UseCaseResultList.Failure -> {
                    news = ModelNewsWithLoading(isLoaded = true)
                    snackbarHostState.showSnackbar(result.error)
                }
                is UseCaseResultList.Success -> {
                    news = ModelNewsWithLoading(isLoaded = true, listNews = result.list.map { googleNewsDTO -> googleNewsDTO.toUI() })
                }
            }
            MailNews::class -> when(val result = getListMailNews.invoke()){
                is UseCaseResultList.Failure -> {
                    news = ModelNewsWithLoading(isLoaded = true)
                    snackbarHostState.showSnackbar(result.error)
                }
                is UseCaseResultList.Success -> {
                    news = ModelNewsWithLoading(isLoaded = true, listNews = result.list.map { mailNewsDTO -> mailNewsDTO.toUI() })
                }
            }
            TassNews::class -> when(val result = getListTassNews.invoke()){
                is UseCaseResultList.Failure -> {
                    news = ModelNewsWithLoading(isLoaded = true)
                    snackbarHostState.showSnackbar(result.error)
                }
                is UseCaseResultList.Success -> {
                    news = ModelNewsWithLoading(isLoaded = true, listNews = result.list.map { tassDTO -> tassDTO.toUI() })
                }
            }
            BBCNews::class -> when(val result = getListBBCNews.invoke(bbcType = selectedBBC ?: BBCType.WORLD)){
                is UseCaseResultList.Failure -> {
                    news = ModelNewsWithLoading(isLoaded = true)
                    snackbarHostState.showSnackbar(result.error)
                }
                is UseCaseResultList.Success -> {
                    news = ModelNewsWithLoading(isLoaded = true, listNews = result.list.map { bbcDTO -> bbcDTO.toUI(selectedBBC ?: BBCType.WORLD) })
                }
            }
            NewYorkNews::class -> when(val result = getListNewYorkNews.invoke(newYorkType = selectedNY ?: NewYorkType.WORLD)){
                is UseCaseResultList.Failure -> {
                    news = ModelNewsWithLoading(isLoaded = true)
                    snackbarHostState.showSnackbar(result.error)
                }
                is UseCaseResultList.Success -> {
                    news = ModelNewsWithLoading(isLoaded = true, listNews = result.list.map { newYorkTimeDTO -> newYorkTimeDTO.toUI(selectedNY ?: NewYorkType.WORLD) })
                }
            }
            else -> Unit
        }
        jobLoadedNews = null
    }

    fun onSelectBBCNews(bbcType: BBCType){
        val index = listChip.firstOrNull { it.type == BBCNews::class }?.let { listChip.indexOf(it) }
        if (index != null && index != -1){
            selectedBBC = bbcType
            onClickClip(index)
        }
    }

    fun onSelectNewYorkNews(newYorkType: NewYorkType){
        val index = listChip.firstOrNull { it.type == NewYorkNews::class }?.let { listChip.indexOf(it) }
        if (index != null && index != -1){
            selectedNY = newYorkType
            onClickClip(index)
        }
    }
}