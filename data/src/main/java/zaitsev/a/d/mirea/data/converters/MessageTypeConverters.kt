package zaitsev.a.d.mirea.data.converters

import androidx.room.TypeConverter
import zaitsev.a.d.mirea.data.type.MessageTypeEntity

class MessageTypeConverters {
    @TypeConverter
    fun toType(value: String) = MessageTypeEntity.valueOf(value)

    @TypeConverter
    fun fromType(value: MessageTypeEntity) = value.name
}