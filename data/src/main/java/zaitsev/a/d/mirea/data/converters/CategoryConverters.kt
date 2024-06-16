package zaitsev.a.d.mirea.data.converters

import androidx.room.TypeConverter

class CategoryConverters {
    @TypeConverter
    fun fromCategory(value: String?): List<String>? {
        return value?.split(";")
    }

    @TypeConverter
    fun categoryToString(category: List<String>?): String? {
        return category?.joinToString(";")
    }
}