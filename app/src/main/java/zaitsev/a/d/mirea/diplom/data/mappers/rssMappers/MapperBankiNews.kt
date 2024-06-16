package zaitsev.a.d.mirea.diplom.data.mappers.rssMappers

import zaitsev.a.d.mirea.diplom.data.rss.BankiNews
import zaitsev.a.d.mirea.domain.models.rss.BankiNewsDTO

fun BankiNewsDTO.toUI(): BankiNews{
    return BankiNews(title = title, description = description, linq = linq, date = date, synopsis = synopsis, bankName = bankName)
}