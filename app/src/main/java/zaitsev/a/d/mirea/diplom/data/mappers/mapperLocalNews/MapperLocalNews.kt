package zaitsev.a.d.mirea.diplom.data.mappers.mapperLocalNews

import zaitsev.a.d.mirea.data.modelSource.local.NewsLocalEntity
import zaitsev.a.d.mirea.diplom.data.rss.AstroBeneNews
import zaitsev.a.d.mirea.diplom.data.rss.BBCNews
import zaitsev.a.d.mirea.diplom.data.rss.BankiNews
import zaitsev.a.d.mirea.diplom.data.rss.GoogleNews
import zaitsev.a.d.mirea.diplom.data.rss.MailNews
import zaitsev.a.d.mirea.diplom.data.rss.ModelNews
import zaitsev.a.d.mirea.diplom.data.rss.NewYorkNews
import zaitsev.a.d.mirea.diplom.data.rss.TassNews
import zaitsev.a.d.mirea.diplom.data.rss.classTypeToNews
import zaitsev.a.d.mirea.diplom.data.rss.getGsonScheme
import zaitsev.a.d.mirea.diplom.presentation.getDateTimeNow

fun ModelNews.toEntity(userID: Long): NewsLocalEntity? {

    val type = when(this){
        is AstroBeneNews -> this::class.simpleName
        is BBCNews -> this::class.simpleName
        is BankiNews -> this::class.simpleName
        is GoogleNews -> this::class.simpleName
        is MailNews -> this::class.simpleName
        is NewYorkNews -> this::class.simpleName
        is TassNews -> this::class.simpleName
    } ?: return null

    val content = this.getGsonScheme

    return NewsLocalEntity(
        user = userID,
        type = type,
        content = content,
        category = when(this){
            is AstroBeneNews -> this.category
            is BBCNews -> listOf(this.category)
            is BankiNews -> null
            is GoogleNews -> null
            is MailNews -> listOf(this.category)
            is NewYorkNews -> this.category
            is TassNews -> this.category
        },
        safeDate = getDateTimeNow(),
        hashCodeModel = this.hashCode()
    )
}

fun NewsLocalEntity.toUI(): ModelNews?{
    val newsUI = type.classTypeToNews(this.content) ?: return null
    return when(newsUI){
        is AstroBeneNews -> newsUI.copy(id = this.id, isLocal = true)
        is BBCNews -> newsUI.copy(id = this.id, isLocal = true)
        is BankiNews -> newsUI.copy(id = this.id, isLocal = true)
        is GoogleNews -> newsUI.copy(id = this.id, isLocal = true)
        is MailNews -> newsUI.copy(id = this.id, isLocal = true)
        is NewYorkNews -> newsUI.copy(id = this.id, isLocal = true)
        is TassNews -> newsUI.copy(id = this.id, isLocal = true)
    }
}