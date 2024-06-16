package zaitsev.a.d.mirea.data.converters

import androidx.room.TypeConverter
import zaitsev.a.d.mirea.data.type.MessageStateEntity

class MessageStateConverters {
    @TypeConverter
    fun toState(value: String) = MessageStateEntity.valueOf(value)

    @TypeConverter
    fun fromState(value: MessageStateEntity) = value.name
}