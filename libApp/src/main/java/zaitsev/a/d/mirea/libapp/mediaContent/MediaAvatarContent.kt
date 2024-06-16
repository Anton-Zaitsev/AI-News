package zaitsev.a.d.mirea.libapp.mediaContent

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.MimeTypeMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import zaitsev.a.d.mirea.libapp.dateNow
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MediaAvatarContent(private val context: Context, private val ioDispatcher: CoroutineDispatcher) {
    companion object {
        private const val EXTENSION_IMAGE = "jpg"
        private const val PATTERN_DATE = "yyyy-MM-dd HH:mm:ss"
        private const val DIR_IMAGE = "avatarImage"
    }

    suspend fun convertBitmapToFile(bitmap: Bitmap): String? {
        return withContext(ioDispatcher){
            val file = getPathFile(EXTENSION_IMAGE) ?: return@withContext null
            try {
                if (file.createNewFile()){
                    FileOutputStream(file).buffered().use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        file.absolutePath
                    }
                }
                else null
            } catch (_: IOException){
                null
            }
        }
    }

    suspend fun convertUriToFile(uri: Uri): String? {
        return withContext(ioDispatcher){
            try {
                val resolver = context.contentResolver
                val extension = if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT))
                    MimeTypeMap.getSingleton().getExtensionFromMimeType(resolver.getType(uri))
                else
                    MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(uri.path?.let { path -> File(path) }).toString())

                val pathFile = extension?.let { extensionFile -> getPathFile(extensionFile) } ?: return@withContext null

                context.contentResolver.openInputStream(uri)?.buffered()?.use { inputStream ->
                    if (pathFile.createNewFile()){
                        FileOutputStream(pathFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                        pathFile.absolutePath
                    }
                    else null
                }
            }catch (_: Exception){
                null
            }
        }
    }

    private fun getPathFile(extension: String): File? {
        val pathDictionary = context.getExternalFilesDir(null) ?: return null
        val dir = File(pathDictionary, DIR_IMAGE)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        else {
            dir.listFiles()?.forEach { files ->
                files.deleteRecursively()
            }
        }
        return File(dir, "/avatar_${dateNow(PATTERN_DATE)}.${extension}")
    }
}