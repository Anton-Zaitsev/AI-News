package zaitsev.a.d.mirea.diplom.data.mappers.rssMappers

import zaitsev.a.d.mirea.diplom.data.rss.GoogleNews
import zaitsev.a.d.mirea.domain.models.rss.GoogleNewsDTO

fun GoogleNewsDTO.toUI(): GoogleNews{
    return GoogleNews(title = title, linq = linq, date = date, description = description, source = source, sourceURL = sourceURL)
}