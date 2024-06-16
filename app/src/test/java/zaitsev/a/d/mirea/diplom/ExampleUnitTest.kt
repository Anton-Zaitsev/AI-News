package zaitsev.a.d.mirea.diplom

import org.junit.Test
import zaitsev.a.d.mirea.diplom.data.rss.ModelNews
import kotlin.reflect.KClass

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val subclasses: List<KClass<*>> = ModelNews::class.sealedSubclasses
        assert(subclasses.size == 5)
    }
}