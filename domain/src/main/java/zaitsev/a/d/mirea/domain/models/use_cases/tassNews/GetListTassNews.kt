package zaitsev.a.d.mirea.domain.models.use_cases.tassNews

import zaitsev.a.d.mirea.domain.models.Failure
import zaitsev.a.d.mirea.domain.models.Success
import zaitsev.a.d.mirea.domain.models.repository.rss.TassRepository
import zaitsev.a.d.mirea.domain.models.rss.TassDTO
import zaitsev.a.d.mirea.domain.models.use_cases.UseCaseResultList
import javax.inject.Inject

class GetListTassNews @Inject constructor(private val repository: TassRepository) {
    suspend operator fun invoke(): UseCaseResultList<TassDTO> {
        return when(val listNews = repository.getNews()){
            is Success -> UseCaseResultList.Success(listNews.value)
            is Failure -> UseCaseResultList.Failure(listNews.reason)
        }
    }
}