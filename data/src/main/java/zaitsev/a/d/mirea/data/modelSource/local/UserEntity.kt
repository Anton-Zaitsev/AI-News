package zaitsev.a.d.mirea.data.modelSource.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(
    tableName = "users",
    indices = [Index(value = ["user_id"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "user_id")
    val userID: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "last_name")
    val lastName: String,
    @ColumnInfo(name = "phone")
    val phone: String,
    @ColumnInfo(name = "password_telegram")
    val passwordTelegram: String,
    @ColumnInfo(name = "using_biometry", defaultValue = "0")
    val usingBiometry: Boolean = false,
    @ColumnInfo(name = "avatar_url", defaultValue = "NULL")
    val avatarURL: String? = null,
    @ColumnInfo(name = "current_user", defaultValue = "1")
    val currentUser: Boolean = true
)