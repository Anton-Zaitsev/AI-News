package zaitsev.a.d.mirea.diplom.presentation.ui.main.newsCell

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.data.rss.MailNews
import zaitsev.a.d.mirea.diplom.presentation.convertToStringDateNews
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import java.net.URL
import java.util.Date

@Composable
fun MailNewsCell(mailNewsData: MailNews, context: Context, navigateToShare: () -> Unit){
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp)
        .clickable(onClick = navigateToShare)
    ) {
        val (image, data) = createRefs()
        AsyncImage(
            model = ImageRequest.Builder(context = context)
                .data(R.drawable.mailnewsbig)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
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

        Column(modifier = Modifier.constrainAs(data){
            linkTo(top = parent.top, bottom = parent.bottom)
            linkTo(start = image.end, end = parent.end, startMargin = 10.dp)
            width = Dimension.fillToConstraints
            height = Dimension.preferredWrapContent
        }) {
            Text(
                text = mailNewsData.category,
                color = Color.Gray,
                maxLines = 1,
                softWrap = true,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = mailNewsData.title,
                color = MaterialTheme.colorScheme.onSecondary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 5.dp)
            )
            Text(
                text = mailNewsData.date.convertToStringDateNews(),
                color = Color.DarkGray
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun MailNewsCellPreview() {
    val data = MailNews(
        title = "Доходы бюджета Астраханской области за пять лет увеличились на 46,6%",
        linq = URL("https://tass.ru/rss/v2.xml?sections=MjU%3D"),
        date = Date(),
        description = "В регионе стали активнее реализовываться социально важные проекты, увеличены различные выплаты и пособия",
        category = "Москва"
    )
    ZaitsevNewsTheme {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp)) {
            MailNewsCell(mailNewsData = data, context = LocalContext.current, navigateToShare = {

            })
        }
    }
}