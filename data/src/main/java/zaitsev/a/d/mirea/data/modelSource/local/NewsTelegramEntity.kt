package zaitsev.a.d.mirea.data.modelSource.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "news",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("user"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class NewsTelegramEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "user")
    val user: Long,
    @ColumnInfo(name = "chatID")
    val chatID: Long,
    @ColumnInfo(name = "superGroupID")
    val superGroupID: Long,
    @ColumnInfo(name = "position")
    val position: Long
)
