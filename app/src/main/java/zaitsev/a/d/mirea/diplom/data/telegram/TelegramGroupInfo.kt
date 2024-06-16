package zaitsev.a.d.mirea.diplom.data.telegram

import org.drinkless.td.libcore.telegram.TdApi
import org.drinkless.td.libcore.telegram.TdApi.MessageSenderUser


data class SubscribersBasicGroup(
    val statusOnline: TdApi.UserStatus,
    val subscriberID: Long,
)

interface TelegramFullGroupInfo {
    val countSubscribers: Int
    val description: String
}
sealed class TelegramGroupInfo(
    override val countSubscribers: Int,
    override val description: String): TelegramFullGroupInfo{

    data class TelegramBasicGroupInfo(
        val listSubscribers: List<SubscribersBasicGroup>,
        override val description: String,
    ): TelegramGroupInfo(countSubscribers = listSubscribers.size, description = description)


    data class TelegramSuperGroupInfo(
        override val countSubscribers: Int,
        override val description: String
    ): TelegramGroupInfo(countSubscribers = countSubscribers, description = description)
}


fun List<SubscribersBasicGroup>.updateValues(info: TdApi.BasicGroupFullInfo): List<SubscribersBasicGroup>{
    return info.members.mapNotNull {
        when(val subscriber = it.memberId){
            is MessageSenderUser -> {
                subscriber.userId
            }
            else -> null
        }?.let { subscriberID ->
            val status = this.firstOrNull { subs -> subs.subscriberID == subscriberID }?.statusOnline ?: TdApi.UserStatusRecently()
            SubscribersBasicGroup(statusOnline = status, subscriberID = subscriberID)
        }
    }
}
fun TdApi.BasicGroupFullInfo.toUI(): TelegramGroupInfo{
    return TelegramGroupInfo.TelegramBasicGroupInfo(listSubscribers = members.mapNotNull {
         when(val subscriber = it.memberId){
            is MessageSenderUser -> {
                subscriber.userId
            }
            else -> null
        }?.let { subscriberID ->
             SubscribersBasicGroup(statusOnline = TdApi.UserStatusRecently(), subscriberID = subscriberID)
        }
    }, description = this.description)
}

fun TdApi.SupergroupFullInfo.toUI(): TelegramGroupInfo {
    return TelegramGroupInfo.TelegramSuperGroupInfo(countSubscribers = this.memberCount, description = this.description)
}