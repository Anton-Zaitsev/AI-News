package zaitsev.a.d.mirea.diplom.widget

import android.content.Context
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import zaitsev.a.d.mirea.diplom.data.mappers.mapperLocalNews.toUI
import zaitsev.a.d.mirea.diplom.data.rss.ModelNews
import zaitsev.a.d.mirea.diplom.di.DatabaseModule

class WidgetNewsRepository(private val context: Context): DataStore<List<ModelNews>> {
    override val data: Flow<List<ModelNews>>
        get() {
            val db = DatabaseModule().provideDatabase(context)
            val userDao = db.userDao()
            val newsDao = db.newsDao()
            return userDao.getCurrentUserIDFlow().transform { id ->
                newsDao.getNewsFlow(id).collect { list ->
                    emit(list.mapNotNull { news -> news.toUI()  })
                }
            }
        }

    override suspend fun updateData(transform: suspend (t: List<ModelNews>) -> List<ModelNews>): List<ModelNews> {
        throw NotImplementedError("Этот метод не реализован из-за не ненадобности")
    }
}