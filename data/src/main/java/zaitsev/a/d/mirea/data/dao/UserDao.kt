package zaitsev.a.d.mirea.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import zaitsev.a.d.mirea.data.modelSource.local.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUser(userEntity: UserEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users")
    fun getUsersListFlow(): Flow<List<UserEntity>>

    @Query("SELECT EXISTS(SELECT * FROM users WHERE current_user = 1)")
    suspend fun isExistUserCurrent(): Boolean

    @Query("SELECT id FROM users WHERE current_user = 1 LIMIT 1")
    suspend fun getCurrentUserID(): Long?

    @Query("SELECT * FROM users WHERE phone =:phone")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT id FROM users WHERE current_user = 1 LIMIT 1")
    fun getCurrentUserIDFlow(): Flow<Long>

    @Query("SELECT id FROM users WHERE user_id = :userID LIMIT 1")
    suspend fun getExistUserID(userID: String): Long

    @Query("UPDATE users SET current_user = 0 WHERE user_id =:userID")
    suspend fun exit(userID: String)

    @Query("UPDATE users SET current_user = 0 WHERE current_user = 1")
    suspend fun removeCurrentUsers()

    @Transaction
    suspend fun addCurrentUser(user: UserEntity){
        removeCurrentUsers()
        val userExist = getUserByPhone(phone = user.phone)
        if (userExist != null){
            updateUser(userExist.copy(avatarURL = user.avatarURL, name = user.name, lastName = user.lastName, currentUser = true))
        } else {
            setUser(user)
        }
    }

    @Query("SELECT * FROM users WHERE current_user = 1 LIMIT 1")
    fun getFlowCurrentUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE current_user = 1 LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Query("UPDATE users SET phone = :phone, name = :name, last_name = :lastName, avatar_url = :avatarURL WHERE user_id = :userID")
    suspend fun updateUserData(userID: String, phone: String, name: String, lastName: String, avatarURL: String?)

    @Query("SELECT EXISTS(SELECT * FROM users WHERE name = :name)")
    suspend fun isExitsName(name: String): Boolean
}