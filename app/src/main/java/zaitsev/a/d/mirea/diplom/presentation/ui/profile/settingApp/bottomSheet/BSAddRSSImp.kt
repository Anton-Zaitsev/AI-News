package zaitsev.a.d.mirea.diplom.presentation.ui.profile.settingApp.bottomSheet

import zaitsev.a.d.mirea.diplom.data.channelRSS.RSSInfoUI

interface BSAddRSSImp {
    suspend fun findRSSChannel(url: String): RSSInfoUI?
    fun addRSS(rss: RSSInfoUI)
    fun dismiss()
}