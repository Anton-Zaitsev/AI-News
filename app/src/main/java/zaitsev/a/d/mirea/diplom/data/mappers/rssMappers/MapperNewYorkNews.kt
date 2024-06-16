package zaitsev.a.d.mirea.diplom.data.mappers.rssMappers

import zaitsev.a.d.mirea.diplom.data.rss.NewYorkNews
import zaitsev.a.d.mirea.domain.models.newsTypes.NewYorkType
import zaitsev.a.d.mirea.domain.models.rss.NewYourTimeDTO


fun NewYourTimeDTO.toUI(newYorkType: NewYorkType): NewYorkNews {
    return NewYorkNews(
        title = this.title,
        linq = this.linq,
        date = this.date,
        description = this.description,
        imageURL = this.imageURL,
        category = if (this.category.isNullOrEmpty())
            listOf(newYorkType.toCategory())
        else requireNotNull(this.category)
    )
}

fun NewYorkType.toCategory(): String{
    return when(this){
        NewYorkType.WORLD -> "World News"
        NewYorkType.BUSINESS -> "Business"
        NewYorkType.TECHNOLOGY -> "Technology"
        NewYorkType.SPORT -> "Sport"
    }
}