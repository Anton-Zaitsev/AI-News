package zaitsev.a.d.mirea.diplom.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.data.rss.AstroBeneNews
import zaitsev.a.d.mirea.diplom.data.rss.BBCNews
import zaitsev.a.d.mirea.diplom.data.rss.BankiNews
import zaitsev.a.d.mirea.diplom.data.rss.GoogleNews
import zaitsev.a.d.mirea.diplom.data.rss.MailNews
import zaitsev.a.d.mirea.diplom.data.rss.ModelNews
import zaitsev.a.d.mirea.diplom.data.rss.NewYorkNews
import zaitsev.a.d.mirea.diplom.data.rss.TassNews
import zaitsev.a.d.mirea.diplom.presentation.theme.colorsWidget
import zaitsev.a.d.mirea.diplom.presentation.ui.MainActivity
import java.io.File
import kotlin.math.roundToInt


class ZaitsevNewsWidget: GlanceAppWidget() {

    companion object {

        suspend fun Context.updateWidgetNews(){
            val manager = GlanceAppWidgetManager(this)
            val widget = ZaitsevNewsWidget()
            val glanceIds = manager.getGlanceIds(widget.javaClass)
            glanceIds.forEach { glanceId ->
                widget.update(this, glanceId)
            }
        }

        private val HORIZONTAL_RECTANGLE = DpSize(250.dp, 100.dp)
        private val BIG_SQUARE = DpSize(250.dp, 250.dp)
    }


    override val sizeMode = SizeMode.Responsive(
        setOf(
            HORIZONTAL_RECTANGLE,
            BIG_SQUARE
        )
    )

    override val stateDefinition: GlanceStateDefinition<List<ModelNews>>
        get() = object: GlanceStateDefinition<List<ModelNews>> {
            override suspend fun getDataStore(
                context: Context,
                fileKey: String
            ): DataStore<List<ModelNews>> {
                return WidgetNewsRepository(context)
            }
            override fun getLocation(context: Context, fileKey: String): File {
                throw NotImplementedError("Этот метод не реализован из-за не ненадобности")
            }
        }



    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val list = currentState<List<ModelNews>>()

            val size = LocalSize.current

            GlanceTheme(colors = colorsWidget) {
                Column(
                    modifier = GlanceModifier.fillMaxSize().background(GlanceTheme.colors.background).padding(8.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Header(list = list)
                    if (list.isEmpty()){
                        NotFound()
                    }
                    else {
                        ListView(list = list, size = size)
                    }
                }
            }
        }
    }

    @Composable
    private fun Header(list: List<ModelNews>){
        Row(modifier = GlanceModifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.Vertical.CenterVertically) {

            Image(
                provider = ImageProvider(R.drawable.ic_saved),
                contentDescription = "Image Saved",
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onSecondary)
            )

            Text(
                text = LocalContext.current.getString(R.string.myNews),
                style = TextStyle(
                    color = GlanceTheme.colors.onSecondary,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = GlanceModifier.padding(start = 5.dp)
            )
            if (list.isNotEmpty()){
                Box(
                    modifier = GlanceModifier.fillMaxWidth().padding(horizontal = 5.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = list.size.toString(),
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = GlanceModifier.padding(horizontal = 5.dp).background(Color.Red).cornerRadius(30.dp)
                    )
                }
            }
        }
    }
    @Composable
    private fun ListView(list: List<ModelNews>, size: DpSize){
        LazyColumn{
            items(list){ item ->

                val logo = when(item){
                    is AstroBeneNews -> R.drawable.astronewsbig
                    is BBCNews -> R.drawable.bbcbig
                    is BankiNews -> R.drawable.bankirubig
                    is GoogleNews -> R.drawable.googlerss
                    is MailNews -> R.drawable.mailnewsbig
                    is NewYorkNews -> R.drawable.newyorknewsbig
                    is TassNews -> R.drawable.tassrss
                }

                Row(modifier = GlanceModifier.fillMaxWidth().padding(5.dp).clickable(
                    actionStartActivity<MainActivity>()
                ),
                    horizontalAlignment = Alignment.Horizontal.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val sizeImage = (size.width.value / 5F).roundToInt()
                    Image(
                        provider = ImageProvider(logo),
                        contentDescription = "My image",
                        modifier = GlanceModifier.size(sizeImage.dp)
                            .cornerRadius((sizeImage / 2F ).roundToInt().dp)
                    )

                    Column(modifier = GlanceModifier.fillMaxWidth().padding(start = 15.dp)) {
                        Text(
                            text = item.title,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSecondary
                            ),
                            maxLines = 2,
                            modifier = GlanceModifier.fillMaxWidth()
                        )

                        Spacer(modifier = GlanceModifier.height(1.dp))

                        Text(
                            text = item.description,
                            style = TextStyle(
                                color = ColorProvider(Color.Gray)
                            ),
                            maxLines = 2,
                            modifier = GlanceModifier.fillMaxWidth()
                        )

                        Spacer(modifier = GlanceModifier.padding(vertical = 3.dp))

                        Box(modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .cornerRadius(0.5.dp)
                            .background(colorProvider = ColorProvider(Color.Gray))
                        ){}
                    }
                }
            }
        }
    }
    @Composable
    private fun NotFound() {
        Column(modifier = GlanceModifier.fillMaxSize(), horizontalAlignment = Alignment.Horizontal.CenterHorizontally, verticalAlignment = Alignment.Vertical.CenterVertically) {

            Text(
                text = LocalContext.current.getString(R.string.ops),
                style = TextStyle(
                    color = GlanceTheme.colors.onSecondary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                modifier = GlanceModifier.fillMaxWidth()
            )
            Spacer(modifier = GlanceModifier.height(3.dp))

            Text(
                text = LocalContext.current.getString(R.string.notFoundLocalNews),
                style = TextStyle(
                    color = GlanceTheme.colors.onSecondary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp
                ),
                modifier = GlanceModifier.fillMaxWidth()
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            Button(
                text = LocalContext.current.getString(R.string.goToNews),
                onClick = actionStartActivity<MainActivity>(),
                colors = ButtonDefaults.buttonColors(backgroundColor = ColorProvider(Color.Transparent), contentColor = GlanceTheme.colors.primary)
            )
        }
    }
}