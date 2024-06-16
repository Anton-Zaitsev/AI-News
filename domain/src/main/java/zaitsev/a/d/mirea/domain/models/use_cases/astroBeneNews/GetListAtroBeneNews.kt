package zaitsev.a.d.mirea.domain.models.use_cases.astroBeneNews

import zaitsev.a.d.mirea.domain.models.Failure
import zaitsev.a.d.mirea.domain.models.Success
import zaitsev.a.d.mirea.domain.models.repository.rss.AstroBeneRepository
import zaitsev.a.d.mirea.domain.models.rss.AstroBeneNewsDTO
import zaitsev.a.d.mirea.domain.models.use_cases.UseCaseResultList
import javax.inject.Inject

class GetListAtroBeneNews @Inject constructor(private val repository: AstroBeneRepository) {
    suspend operator fun invoke(): UseCaseResultList<AstroBeneNewsDTO>{
        return when(val listNews = repository.getNews()){
            is Success -> UseCaseResultList.Success(listNews.value)
            is Failure -> UseCaseResultList.Failure(listNews.reason)
        }
    }
}