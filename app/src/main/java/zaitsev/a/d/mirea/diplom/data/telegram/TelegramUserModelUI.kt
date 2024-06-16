package zaitsev.a.d.mirea.diplom.data.telegram

import org.drinkless.td.libcore.telegram.TdApi

data class TelegramUserModelUI(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val userName: String,
    val phone: String,
    val profilePhoto: String?
)


fun TdApi.User.toUI(): TelegramUserModelUI{
    return TelegramUserModelUI(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        userName = this.username,
        phone = this.phoneNumber,
        profilePhoto = this.profilePhoto?.small?.local?.path?.ifEmpty { null }
    )
}