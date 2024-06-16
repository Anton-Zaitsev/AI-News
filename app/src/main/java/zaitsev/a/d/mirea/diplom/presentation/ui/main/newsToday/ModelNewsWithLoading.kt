package zaitsev.a.d.mirea.diplom.presentation.ui.main.newsToday

import zaitsev.a.d.mirea.diplom.data.rss.ModelNews

data class ModelNewsWithLoading(
    val listNews: List<ModelNews> = emptyList(),
    val isLoaded: Boolean = false
)
