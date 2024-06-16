package zaitsev.a.d.mirea.domain.models.repository.rss

import zaitsev.a.d.mirea.domain.models.ResultCoroutines
import zaitsev.a.d.mirea.domain.models.newsTypes.NewYorkType
import zaitsev.a.d.mirea.domain.models.rss.NewYourTimeDTO

interface NewYorkNewsRepository {
    suspend fun getNews(newYorkType: NewYorkType): ResultCoroutines<List<NewYourTimeDTO>, String>
}