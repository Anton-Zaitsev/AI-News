package zaitsev.a.d.mirea.diplom.data.mappers.rssMappers

import zaitsev.a.d.mirea.diplom.data.rss.TassNews
import zaitsev.a.d.mirea.domain.models.rss.TassDTO

fun TassDTO.toUI(): TassNews {
    return TassNews(
        title = title,
        linq = linq,
        date = date,
        description = description,
        imageURL = imageURL,
        category = category
    )
}