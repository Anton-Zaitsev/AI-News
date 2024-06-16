package zaitsev.a.d.mirea.diplom.presentation.ui.main.newsCell

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.data.rss.TassNews
import zaitsev.a.d.mirea.diplom.presentation.convertToStringDateNews
import zaitsev.a.d.mirea.diplom.presentation.shimmerEffect
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import java.net.URL
import java.util.Date

@Composable
fun TassNewsCell(tassData: TassNews, context: Context, navigateToShare: () -> Unit){
    SubcomposeAsyncImage(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable(
                onClick = navigateToShare
            ),
        model = ImageRequest.Builder(context = context)
            .data(tassData.imageURL ?: R.drawable.tassnewsbig)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = stringResource(id = R.string.imageAvatar),
        content = {
            val state = painter.state
            val painter = painter

            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (image, data) = createRefs()
                if (state is AsyncImagePainter.State.Loading) {
                    Box(modifier = Modifier
                        .constrainAs(image) {
                            linkTo(top = data.top, bottom = data.bottom)
                            start.linkTo(parent.start)
                            width = Dimension.percent(0.2f)
                            height = Dimension.ratio("1:1")
                        }
                        .clip(RoundedCornerShape(15.dp))
                        .shimmerEffect())
                }
                else{
                    Image(
                        painter = if (state is AsyncImagePainter.State.Success) painter else painterResource(id = R.drawable.tassnewsbig),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .constrainAs(image) {
                                linkTo(top = data.top, bottom = data.bottom)
                                start.linkTo(parent.start)
                                width = Dimension.percent(0.2f)
                                height = Dimension.ratio("1:1")
                            }
                            .clip(RoundedCornerShape(15.dp))
                    )
                }

                Column(modifier = Modifier.constrainAs(data){
                    linkTo(top = parent.top, bottom = parent.bottom)
                    linkTo(start = image.end, end = parent.end, startMargin = 10.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.preferredWrapContent
                }) {
                    Text(
                        text = tassData.category.joinToString(", "),
                        color = Color.Gray,
                        maxLines = 1,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = tassData.title,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 5.dp),
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                    Text(
                        text = tassData.date.convertToStringDateNews(),
                        color = Color.DarkGray
                    )
                }
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun TassNewsPreview() {
    val urlImage = "https://cdn-storage-media.tass.ru/resize/376x248/tass_media/2023/10/05/F/1696491063351653_FoZI5dRF.jpg"
    val data = TassNews(
        title = "Доходы бюджета Астраханской области за пять лет увеличились на 46,6%",
        linq = URL("https://tass.ru/rss/v2.xml?sections=MjU%3D"),
        date = Date(),
        description = "В регионе стали активнее реализовываться социально важные проекты, увеличены различные выплаты и пособия",
        imageURL = urlImage,
        category = listOf("Москва", "Экономика и бизнес", "Северный Кавказ")
    )
    ZaitsevNewsTheme {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp)) {
            TassNewsCell(tassData = data, context = LocalContext.current, navigateToShare = {

            })
        }
    }
}