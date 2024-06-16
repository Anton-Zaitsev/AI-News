package zaitsev.a.d.mirea.domain.models.use_cases.newYorkNews

import zaitsev.a.d.mirea.domain.models.Failure
import zaitsev.a.d.mirea.domain.models.Success
import zaitsev.a.d.mirea.domain.models.newsTypes.NewYorkType
import zaitsev.a.d.mirea.domain.models.repository.rss.NewYorkNewsRepository
import zaitsev.a.d.mirea.domain.models.rss.NewYourTimeDTO
import zaitsev.a.d.mirea.domain.models.use_cases.UseCaseResultList
import javax.inject.Inject

class GetListNewYorkNews @Inject constructor(private val newYourRepository: NewYorkNewsRepository) {
    suspend operator fun invoke(newYorkType: NewYorkType): UseCaseResultList<NewYourTimeDTO> {
        return when(val listNews = newYourRepository.getNews(newYorkType)){
            is Success -> UseCaseResultList.Success(listNews.value)
            is Failure -> UseCaseResultList.Failure(listNews.reason)
        }
    }
}