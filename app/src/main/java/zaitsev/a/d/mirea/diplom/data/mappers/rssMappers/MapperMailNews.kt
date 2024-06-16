package zaitsev.a.d.mirea.diplom.data.mappers.rssMappers

import zaitsev.a.d.mirea.diplom.data.rss.MailNews
import zaitsev.a.d.mirea.domain.models.rss.MailNewsDTO

fun MailNewsDTO.toUI(): MailNews{
    return MailNews(title = title, linq = linq, date = date, description = description, category = category)
}