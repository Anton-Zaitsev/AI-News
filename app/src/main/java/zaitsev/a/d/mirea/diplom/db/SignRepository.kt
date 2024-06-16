package zaitsev.a.d.mirea.diplom.db

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import zaitsev.a.d.mirea.data.dao.UserDao
import zaitsev.a.d.mirea.diplom.constApp.Constants
import zaitsev.a.d.mirea.diplom.db.dataUI.UserUI
import zaitsev.a.d.mirea.diplom.db.mapper.toEntity
import zaitsev.a.d.mirea.diplom.db.mapper.toUserUI
import zaitsev.a.d.mirea.diplom.secret.Secrets
import zaitsev.a.d.mirea.libapp.passwordEncrypter.PasswordEncrypted
import zaitsev.a.d.mirea.libapp.passwordEncrypter.SecretKey

class SignRepository(
    private val userDao: UserDao,
    private val passwordEncrypted: PasswordEncrypted
) {

    private val secretKey by lazy {
        val secret = Secrets()
        SecretKey(
            key = secret.getSecretKey(Constants.ZAITSEV_NEWS_PACKAGE_NAME),
            salt = secret.getSalt(Constants.ZAITSEV_NEWS_PACKAGE_NAME),
            iv = secret.getIV(Constants.ZAITSEV_NEWS_PACKAGE_NAME)
        )
    }

    suspend fun saveUser(user: UserUI){
        val userEntity = user.toEntity(passwordEncrypted = passwordEncrypted, secretKey = secretKey)
        userDao.addCurrentUser(userEntity)
    }

    fun getListFlowUser(): Flow<List<UserUI>> {
        return userDao.getUsersListFlow().map { list ->
            list.map {
                it.toUserUI(
                    passwordEncrypted = passwordEncrypted,
                    secretKey = secretKey
                )
            } }
    }
}

