package zaitsev.a.d.mirea.libapp.pagingGallery

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class PagingGallery(private val context: Context, private val ioDispatcher: CoroutineDispatcher): PagingSource<Int, Uri>() {

    override fun getRefreshKey(state: PagingState<Int, Uri>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Uri> {
        val page = params.key ?: 0
        return withContext(ioDispatcher){
            try {
                val listImages = loadImagesFromStorage(
                    context = context,
                    limit = params.loadSize,
                    offset = page * params.loadSize
                )
                LoadResult.Page(
                    data = listImages,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (listImages.isEmpty()) null else page + 1
                )
            }catch (e: Exception){
                LoadResult.Error(e)
            }
        }
    }


    private fun loadImagesFromStorage(
        context: Context,
        limit: Int,
        offset: Int
    ): List<Uri> {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val listOfAllImages = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val orderBy = MediaStore.Images.Media.DATE_TAKEN

        try {
            context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                "$orderBy DESC"
            )?.use { cursorData ->
                val urlColumnIndex = cursorData.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

                var count = 0
                while (cursorData.moveToNext() && count < limit) {
                    if (count >= offset) {
                        val contentUri =
                            ContentUris.withAppendedId(uri, cursorData.getLong(urlColumnIndex))
                        listOfAllImages.add(contentUri)
                    }
                    count++
                }
            }
        } catch (e: Exception) {
            // Логирование ошибки
            Log.d("PagingGallery", "Error loading images from storage", e)
        }

        return listOfAllImages
    }
}