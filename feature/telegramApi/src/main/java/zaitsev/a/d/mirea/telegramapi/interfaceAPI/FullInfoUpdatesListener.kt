package zaitsev.a.d.mirea.telegramapi.interfaceAPI

import org.drinkless.td.libcore.telegram.TdApi

interface FullInfoUpdatesListener {
    fun onBasicGroupFullInfoUpdated(groupId: Long, info: TdApi.BasicGroupFullInfo)
    fun onSupergroupFullInfoUpdated(groupId: Long, info: TdApi.SupergroupFullInfo)
}