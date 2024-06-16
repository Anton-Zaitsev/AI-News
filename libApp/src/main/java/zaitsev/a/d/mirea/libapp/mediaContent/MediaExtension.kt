package zaitsev.a.d.mirea.libapp.mediaContent

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.database.getStringOrNull


fun Uri.toFilePath(context: Context): String?{
    if (ContentResolver.SCHEME_FILE == this.scheme){
        return this.path
    }else {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        context.contentResolver.query(this, projection, null, null, null)?.let{ cursor ->
            try {
                cursor.use { usedCursor ->
                    if (usedCursor.moveToFirst()) {
                        val columnIndex = usedCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        return usedCursor.getStringOrNull(columnIndex)
                    }
                }
            }catch (e: Exception){
                return null
            }
        }
        return null
    }
}