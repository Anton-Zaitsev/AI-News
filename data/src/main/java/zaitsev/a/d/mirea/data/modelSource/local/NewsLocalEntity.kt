package zaitsev.a.d.mirea.data.modelSource.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import zaitsev.a.d.mirea.data.converters.CategoryConverters
import zaitsev.a.d.mirea.data.converters.DateConverters
import java.util.Date


@Entity(
    tableName = "news",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("user"),
        onDelete = ForeignKey.CASCADE
    )]
)
@TypeConverters(DateConverters::class, CategoryConverters::class)
data class NewsLocalEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "user")
    val user: Long,
    @ColumnInfo(name = "type_news")
    val type: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "category")
    val category: List<String>?,
    @ColumnInfo(name = "save_date")
    val safeDate: Date,
    @ColumnInfo(name = "hash_code")
    val hashCodeModel: Int
)
