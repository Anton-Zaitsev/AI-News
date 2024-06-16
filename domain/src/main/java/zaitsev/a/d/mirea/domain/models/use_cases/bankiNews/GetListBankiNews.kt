package zaitsev.a.d.mirea.domain.models.use_cases.bankiNews

import zaitsev.a.d.mirea.domain.models.Failure
import zaitsev.a.d.mirea.domain.models.Success
import zaitsev.a.d.mirea.domain.models.repository.rss.BankiNewsRepository
import zaitsev.a.d.mirea.domain.models.rss.BankiNewsDTO
import zaitsev.a.d.mirea.domain.models.use_cases.UseCaseResultList
import javax.inject.Inject

class GetListBankiNews @Inject constructor(private val repository: BankiNewsRepository) {
    suspend operator fun invoke(): UseCaseResultList<BankiNewsDTO> {
        return when(val listNews = repository.getNews()){
            is Success -> UseCaseResultList.Success(listNews.value)
            is Failure -> UseCaseResultList.Failure(listNews.reason)
        }
    }
}