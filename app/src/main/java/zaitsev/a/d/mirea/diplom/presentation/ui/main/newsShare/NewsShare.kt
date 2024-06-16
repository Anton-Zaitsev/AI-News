package zaitsev.a.d.mirea.diplom.presentation.ui.main.newsShare

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.constApp.Constants.getImageByType
import zaitsev.a.d.mirea.diplom.constApp.Constants.getTitleByType
import zaitsev.a.d.mirea.diplom.data.rss.AstroBeneNews
import zaitsev.a.d.mirea.diplom.data.rss.BBCNews
import zaitsev.a.d.mirea.diplom.data.rss.BankiNews
import zaitsev.a.d.mirea.diplom.data.rss.GoogleNews
import zaitsev.a.d.mirea.diplom.data.rss.MailNews
import zaitsev.a.d.mirea.diplom.data.rss.NewYorkNews
import zaitsev.a.d.mirea.diplom.data.rss.TassNews
import zaitsev.a.d.mirea.diplom.presentation.convertToStringDateNews
import zaitsev.a.d.mirea.diplom.presentation.isNotNull
import zaitsev.a.d.mirea.diplom.presentation.isNull
import zaitsev.a.d.mirea.diplom.presentation.shimmerEffect
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import zaitsev.a.d.mirea.diplom.presentation.ui.components.NavigationBarDefault
import zaitsev.a.d.mirea.diplom.presentation.ui.components.translateBottomSheet.TranslateBottomSheet
import zaitsev.a.d.mirea.speachtextcompose.TextHighlightBuilder
import zaitsev.a.d.mirea.speachtextcompose.view.TTSText
import java.util.Locale


private val cardChipNewsCategoryColor @Composable get() =
    CardDefaults.cardColors(containerColor = Color.DarkGray, contentColor = Color.White)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun NewsShare(
    navController: NavHostController,
    viewModel: NewsShareViewModel
){

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(viewModel.lifecycleEventSpeech)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(viewModel.lifecycleEventSpeech)
        }
    }

    NavigationBarDefault(
        navHostController = navController,
        contentActions = {


            if (viewModel.hashCodeNews.isNotNull && viewModel.mutableNews != null){
                val isLocalSave = requireNotNull(viewModel.mutableNews).isLocal

                IconButton(onClick = viewModel::safeLocal, colors = IconButtonDefaults.iconButtonColors(
                    contentColor = if (!isLocalSave) MaterialTheme.colorScheme.primary else Color.Red
                )) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = if (isLocalSave) R.drawable.ic_heart_fill else R.drawable.ic_heart),
                        contentDescription = "Safe Local"
                    )
                }
            }

            IconButton(onClick = viewModel::speakTextNews) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_voice),
                    contentDescription = "Voice Speek"
                )
            }

            Box(modifier = Modifier
                .clip(CircleShape)
                .combinedClickable(
                    onClick = viewModel::translateNews,
                    onLongClick = viewModel::getListTranslate,
                    enabled = viewModel.isInitTranslateModel
                )
                .padding(5.dp),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_translate),
                    contentDescription = "Translate",
                    tint = Color.Unspecified
                )
            }

            IconButton(onClick = { /* do something */ }) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share"
                )
            }
        },
        content = { paddingValues ->
            if (viewModel.mutableNews != null) {
                val news = requireNotNull(viewModel.mutableNews)

                val scroll = rememberLazyListState()
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 15.dp),
                    state = scroll
                ) {


                    val listCategory = when(news){
                        is AstroBeneNews -> news.category
                        is BankiNews -> null
                        is GoogleNews -> null
                        is MailNews -> listOf(news.category)
                        is TassNews -> news.category
                        is BBCNews -> listOf(news.category)
                        is NewYorkNews -> news.category
                    }

                    val exitsImageNews =
                        (news is TassNews && news.imageURL != null) ||
                                (news is BBCNews && news.imageURL != null) ||
                                (news is NewYorkNews && news.imageURL != null)

                    if (listCategory != null){
                        item {
                            FlowRow(modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 15.dp, bottom = 10.dp, start = 5.dp, end = 5.dp),
                                horizontalArrangement = spacedBy(5.dp),
                                verticalArrangement = spacedBy(5.dp)
                            ) {
                                listCategory.forEach { category ->
                                    Card(colors = cardChipNewsCategoryColor, shape = RoundedCornerShape(20.dp)) {
                                        Text(text = category.replaceFirstChar { it.uppercase() }, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                                    }
                                }
                            }
                        }
                    }

                    if (exitsImageNews){

                        val image = requireNotNull(
                            when(news){
                                is TassNews -> news.imageURL
                                is BBCNews -> news.imageURL
                                is NewYorkNews -> news.imageURL
                                else -> throw Exception("Не найдено фотография для новостей")
                            }
                        )

                        item {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                SubcomposeAsyncImage(
                                    modifier = Modifier.weight(1f),
                                    model = ImageRequest.Builder(context = LocalContext.current)
                                        .data(image)
                                        .crossfade(true)
                                        .diskCachePolicy(CachePolicy.ENABLED)
                                        .build(),
                                    contentDescription = stringResource(id = R.string.imageNews),
                                    content = {
                                        val state = painter.state
                                        val painter = painter

                                        when(state){
                                            is AsyncImagePainter.State.Loading -> {
                                                Box(modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(1f)
                                                    .padding(top = if (listCategory.isNull) 15.dp else 0.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .shimmerEffect()
                                                )
                                            }
                                            is AsyncImagePainter.State.Success -> {
                                                Image(
                                                    painter = painter,
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .aspectRatio(1f)
                                                        .padding(top = if (listCategory.isNull) 15.dp else 0.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                )
                                            }
                                            else -> Unit
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    item {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                            .padding(top = if (exitsImageNews) 10.dp else if (listCategory.isNotNull) 0.dp else 15.dp),
                            horizontalArrangement = spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            news::class.run {
                                val name = getTitleByType()
                                getImageByType()?.let { imageData ->
                                    Image(
                                        painter = painterResource(id = imageData),
                                        contentDescription = stringResource(id = name)
                                    )
                                }
                                Text(text = stringResource(id = name), color = Color.Gray)
                            }
                        }
                    }

                    item {
                        val tag = viewModel.translate?.selectedSourceLanguage?.language?.code ?: requireNotNull(viewModel.mutableNews).language
                        val locale = Locale.forLanguageTag(tag)
                        Text(
                            text = requireNotNull(viewModel.mutableNews).date.convertToStringDateNews(locale),
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        )
                    }

                    item {
                        TTSText(
                            textHighlightBuilder = TextHighlightBuilder(
                                text = news.title,
                                index = viewModel.indexSpeakTitle,
                                style = SpanStyle(color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold, fontSize = (MaterialTheme.typography.titleLarge.fontSize.value + 10).sp)
                            ),
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(bottom = 10.dp, start = 5.dp, end = 5.dp),
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                    }


                    item {
                        TTSText(
                            textHighlightBuilder = TextHighlightBuilder(
                                text = news.description,
                                index = viewModel.indexSpeakDescription,
                                style = SpanStyle(color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold, fontSize = (MaterialTheme.typography.titleMedium.fontSize.value + 10).sp)
                            ),
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(5.dp),
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        )
                    }
                }

                val sheetState = rememberModalBottomSheetState()
                if (viewModel.visibleBMTranslate){
                    TranslateBottomSheet(
                        stateSheet = sheetState,
                        model = viewModel.translate,
                        onSelectTarget = viewModel::onSelectTargetLanguage,
                        deleteModel = viewModel::onDeleteLanguage,
                        dismiss = {
                            viewModel.visibleBMTranslate = false
                        }
                    )
                }
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun NewsSharePreview() {
    ZaitsevNewsTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            NewsShare(
                navController = rememberNavController(),
                viewModel = hiltViewModel()
            )
        }
    }
}