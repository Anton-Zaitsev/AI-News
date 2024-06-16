package zaitsev.a.d.mirea.data.type

import androidx.annotation.Keep

@Keep
enum class MessageTypeEntity {
    TextMessage,
    AudioMessage,
    PhotoMessage,
    NewsMessage
}