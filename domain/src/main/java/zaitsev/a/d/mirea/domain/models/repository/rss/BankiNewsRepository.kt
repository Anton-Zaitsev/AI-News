package zaitsev.a.d.mirea.domain.models.repository.rss

import zaitsev.a.d.mirea.domain.models.ResultCoroutines
import zaitsev.a.d.mirea.domain.models.rss.BankiNewsDTO

interface BankiNewsRepository {
    suspend fun getNews(): ResultCoroutines<List<BankiNewsDTO>, String>
}