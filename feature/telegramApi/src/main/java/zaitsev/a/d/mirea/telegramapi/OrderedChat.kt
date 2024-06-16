package zaitsev.a.d.mirea.telegramapi

import org.drinkless.td.libcore.telegram.TdApi

class OrderedChat(
    val chatId: Long,
    val position: TdApi.ChatPosition,
    val isChannel: Boolean,
) : Comparable<OrderedChat> {

    override fun compareTo(other: OrderedChat): Int {
        if (this.position.order != other.position.order) {
            return if (other.position.order < this.position.order) -1 else 1
        }
        return if (this.chatId != other.chatId) {
            if (other.chatId < this.chatId) -1 else 1
        } else 0
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other !is OrderedChat) {
            return false
        }
        return this.chatId == other.chatId && this.position.order == other.position.order;
    }

    override fun hashCode(): Int {
        return (position.order + chatId).hashCode()
    }
}