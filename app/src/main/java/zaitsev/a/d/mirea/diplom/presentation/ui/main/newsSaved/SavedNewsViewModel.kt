package zaitsev.a.d.mirea.diplom.presentation.ui.main.newsSaved

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import zaitsev.a.d.mirea.diplom.db.NewsLocalRepository
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsToday.ModelNewsWithLoading
import zaitsev.a.d.mirea.diplom.widget.ZaitsevNewsWidget.Companion.updateWidgetNews
import javax.inject.Inject

@HiltViewModel
class SavedNewsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Lazy<Context>,
    private val newsLocalRepository: NewsLocalRepository,
): ViewModel() {

    private val mutableNews = MutableStateFlow(ModelNewsWithLoading())
    val newsLocal = mutableNews.asStateFlow()

    init {
        viewModelScope.launch {
            newsLocalRepository.getFlowListLocalNews()?.collect { listNews ->
                mutableNews.emit(ModelNewsWithLoading(listNews = listNews, isLoaded = true))
            }
        }
    }

    fun deleteAll(){
        viewModelScope.launch {
            newsLocalRepository.deleteAllLocalNews()
            appContext.get().updateWidgetNews()
        }
    }
}