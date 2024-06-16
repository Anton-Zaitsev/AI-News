package zaitsev.a.d.mirea.domain.models.repository.rss

import zaitsev.a.d.mirea.domain.models.ResultCoroutines
import zaitsev.a.d.mirea.domain.models.newsTypes.BBCType
import zaitsev.a.d.mirea.domain.models.rss.BBCNewsDTO

interface BBCNewsRepository {
    suspend fun getNews(bbcType: BBCType): ResultCoroutines<List<BBCNewsDTO>, String>
}