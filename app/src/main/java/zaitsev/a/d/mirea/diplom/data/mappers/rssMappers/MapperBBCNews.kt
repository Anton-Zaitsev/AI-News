package zaitsev.a.d.mirea.diplom.data.mappers.rssMappers

import zaitsev.a.d.mirea.diplom.data.rss.BBCNews
import zaitsev.a.d.mirea.domain.models.newsTypes.BBCType
import zaitsev.a.d.mirea.domain.models.rss.BBCNewsDTO


fun BBCNewsDTO.toUI(bbcType: BBCType): BBCNews {
    return BBCNews(
        title = this.title,
        linq = this.linq,
        date = this.date,
        description = this.description,
        imageURL = this.imageURL,
        category = bbcType.toCategory()
    )
}

fun BBCType.toCategory(): String{
    return when(this){
        BBCType.WORLD -> "World News"
        BBCType.EDUCATION -> "Education"
        BBCType.POLITICS -> "Politics"
        BBCType.TECHNOLOGY -> "Technology"
        BBCType.HEALTH -> "Health"
    }
}