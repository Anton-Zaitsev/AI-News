package zaitsev.a.d.mirea.diplom.presentation.ui.profile.settingApp

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import zaitsev.a.d.mirea.diplom.data.channelRSS.RSSInfoUI
import zaitsev.a.d.mirea.diplom.data.mappers.rssChannel.toUI
import zaitsev.a.d.mirea.diplom.db.RssLocalChannelRepository
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.settingApp.bottomSheet.BSAddRSSImp
import zaitsev.a.d.mirea.domain.models.Failure
import zaitsev.a.d.mirea.domain.models.Success
import zaitsev.a.d.mirea.domain.models.repository.rss.RSSChannelRepository
import javax.inject.Inject

@HiltViewModel
class SettingAppViewModel @Inject constructor(
    private val snackbarHostState: SnackbarHostState,
    private val rssChannelRepository: RSSChannelRepository,
    private val rssLocalChannelRepository: RssLocalChannelRepository
): ViewModel() {

    private val mutableRssList = MutableStateFlow<List<RSSInfoUI>>(listOf())
    val rssList = mutableRssList.asStateFlow()

    var visibleBSAddRss by mutableStateOf(false)
        private set

    val bsAddRSS by lazy {
        object : BSAddRSSImp {
            override suspend fun findRSSChannel(url: String): RSSInfoUI? {
                return when(val rss = rssChannelRepository.getInfoRSSChannel(url = url)){
                    is Failure -> {
                        viewModelScope.launch {
                            snackbarHostState.showSnackbar(rss.reason)
                        }
                        null
                    }
                    is Success -> rss.value.toUI()
                }
            }

            override fun addRSS(rss: RSSInfoUI) {
                dismiss()
                viewModelScope.launch {
                    val isSave = rssLocalChannelRepository.saveLocal(rss = rss)
                    snackbarHostState.showSnackbar(if (isSave) "RSS канал успешно сохранен" else "Не удалось сохранить RSS канал")
                }
            }

            override fun dismiss() {
                visibleBSAddRss = false
            }
        }
    }


    init {
        viewModelScope.launch {
            rssLocalChannelRepository.getFlowListRSS().collect { list ->
                mutableRssList.emit(list)
            }
        }
    }

    fun visibleBSAddRes(){
        visibleBSAddRss = true
    }

    fun removeRSS(rss: RSSInfoUI){
        viewModelScope.launch {
            rssLocalChannelRepository.removeFromLocal(rss = rss)
        }
    }
}