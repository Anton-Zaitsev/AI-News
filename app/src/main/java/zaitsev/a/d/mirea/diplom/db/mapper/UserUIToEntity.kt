package zaitsev.a.d.mirea.diplom.db.mapper

import zaitsev.a.d.mirea.data.modelSource.local.UserEntity
import zaitsev.a.d.mirea.diplom.db.dataUI.UserUI
import zaitsev.a.d.mirea.libapp.passwordEncrypter.PasswordEncrypted
import zaitsev.a.d.mirea.libapp.passwordEncrypter.SecretKey

fun UserUI.toEntity(passwordEncrypted: PasswordEncrypted, secretKey: SecretKey): UserEntity {
    return UserEntity(
        id = this.id,
        userID = this.userID,
        name = this.name,
        lastName = this.lastName,
        phone = this.phone.filter { it.isDigit() },
        passwordTelegram = passwordEncrypted.encryptPassword(this.password, secretKey) ?: this.password,
        avatarURL = this.avatarURL,
        currentUser = true
    )
}

fun UserEntity.toUserUI(passwordEncrypted: PasswordEncrypted, secretKey: SecretKey): UserUI{
    return UserUI(
        id = this.id,
        userID = this.userID,
        name = this.name,
        lastName = this.lastName,
        phone = this.phone,
        password = passwordEncrypted.decryptPassword(this.passwordTelegram, secretKey = secretKey) ?: this.passwordTelegram,
        avatarURL = this.avatarURL,
    )
}