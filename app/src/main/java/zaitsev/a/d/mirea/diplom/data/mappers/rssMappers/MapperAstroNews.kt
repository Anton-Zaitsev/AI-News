package zaitsev.a.d.mirea.diplom.data.mappers.rssMappers

import zaitsev.a.d.mirea.diplom.data.rss.AstroBeneNews
import zaitsev.a.d.mirea.domain.models.rss.AstroBeneNewsDTO

fun AstroBeneNewsDTO.toUI(): AstroBeneNews{
    return AstroBeneNews(title = title, description =  description, linq = linq, date = date, category = category)
}