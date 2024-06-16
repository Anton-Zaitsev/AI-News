package zaitsev.a.d.mirea.diplom.data.mappers.rssChannel

import zaitsev.a.d.mirea.data.modelSource.local.RssChannelEntity
import zaitsev.a.d.mirea.diplom.data.channelRSS.RSSInfoUI
import zaitsev.a.d.mirea.domain.models.rss.RRSInfoDTO

fun RRSInfoDTO.toUI(): RSSInfoUI {
    return RSSInfoUI(title = title, description = description, ling = ling, image = image)
}

fun RSSInfoUI.toEntity(user: Long): RssChannelEntity {
   return RssChannelEntity(
       id = id,
       user = user,
       title = title,
       description = description,
       ling = ling,
       image = image
   )
}

fun RssChannelEntity.toUI(): RSSInfoUI {
    return RSSInfoUI(id = id, title = title, description = description, ling = ling, image = image)
}