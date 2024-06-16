package zaitsev.a.d.mirea.diplom.db

import dagger.Lazy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import zaitsev.a.d.mirea.data.dao.UserDao
import zaitsev.a.d.mirea.diplom.constApp.Constants
import zaitsev.a.d.mirea.diplom.db.dataUI.UserUI
import zaitsev.a.d.mirea.diplom.db.mapper.toUserUI
import zaitsev.a.d.mirea.diplom.secret.Secrets
import zaitsev.a.d.mirea.libapp.passwordEncrypter.PasswordEncrypted
import zaitsev.a.d.mirea.libapp.passwordEncrypter.SecretKey

class UserRepository(
    private val userDao: UserDao,
    private val passwordEncrypted: Lazy<PasswordEncrypted>
) {
    suspend fun existUser(): Boolean{
        return userDao.isExistUserCurrent()
    }
    suspend fun exitUser(userID: String){
        userDao.exit(userID)
    }
    suspend fun getUserCurrent(): UserUI?{
        val secret = Secrets()
        val secretKey = SecretKey(
            key = secret.getSecretKey(Constants.ZAITSEV_NEWS_PACKAGE_NAME),
            salt = secret.getSalt(Constants.ZAITSEV_NEWS_PACKAGE_NAME),
            iv = secret.getIV(Constants.ZAITSEV_NEWS_PACKAGE_NAME)
        )
       return userDao.getCurrentUser()?.toUserUI(passwordEncrypted = passwordEncrypted.get(), secretKey = secretKey)
    }

    fun getFlowUserCurrent(): Flow<UserUI?>{
        val secret = Secrets()
        val secretKey = SecretKey(
            key = secret.getSecretKey(Constants.ZAITSEV_NEWS_PACKAGE_NAME),
            salt = secret.getSalt(Constants.ZAITSEV_NEWS_PACKAGE_NAME),
            iv = secret.getIV(Constants.ZAITSEV_NEWS_PACKAGE_NAME)
        )
        return userDao.getFlowCurrentUser().map { user ->
            user?.toUserUI(passwordEncrypted = passwordEncrypted.get(), secretKey = secretKey)
        }
    }

    suspend fun safeEditUser(userUI: UserUI){
        userDao.updateUserData(
            userID = userUI.userID,
            phone = userUI.phone.filter { it.isDigit() },
            name = userUI.name,
            lastName = userUI.lastName,
            avatarURL = userUI.avatarURL
        )
    }
}