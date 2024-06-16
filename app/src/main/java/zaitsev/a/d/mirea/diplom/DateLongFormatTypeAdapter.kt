package zaitsev.a.d.mirea.diplom

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import zaitsev.a.d.mirea.diplom.presentation.getDateTimeNow
import java.util.Date


class DateLongFormatTypeAdapter : TypeAdapter<Date?>() {
    override fun write(outValue: JsonWriter, value: Date?) {
        try {
            if (value != null) outValue.value(value.time)
            else outValue.value(getDateTimeNow().time)
        }
        catch (_: Exception){
            outValue.value(getDateTimeNow().time)
        }
    }

    override fun read(inValue: JsonReader): Date {
        return try {
            Date(inValue.nextLong())
        }
        catch (_: Exception){
            getDateTimeNow()
        }
    }
}