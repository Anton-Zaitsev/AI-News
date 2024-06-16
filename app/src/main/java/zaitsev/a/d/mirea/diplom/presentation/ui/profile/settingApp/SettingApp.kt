package zaitsev.a.d.mirea.diplom.presentation.ui.profile.settingApp

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.data.channelRSS.RSSInfoUI
import zaitsev.a.d.mirea.diplom.presentation.shimmerEffect
import zaitsev.a.d.mirea.diplom.presentation.theme.Orange
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import zaitsev.a.d.mirea.diplom.presentation.ui.components.NavigationCenterBarDefault
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.settingApp.bottomSheet.BSAddRSS
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingApp(navHostController: NavHostController, viewModel: SettingAppViewModel = hiltViewModel()) {

    val width = LocalConfiguration.current.screenWidthDp

    NavigationCenterBarDefault(navHostController = navHostController, title = stringResource(id = R.string.appNameProfile)) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 15.dp)) {


            Text(
                text = "Ваши RSS каналы".uppercase(Locale.getDefault()),
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                modifier = Modifier.padding(top = 10.dp, start = 15.dp, bottom = 5.dp, end = 15.dp)
            )

            Column(modifier = Modifier
                .fillMaxWidth()
                .heightIn(0.dp, width.dp)
                .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(13.dp))
                .padding(horizontal = 10.dp)) {

                val listRSSLocal by viewModel.rssList.collectAsStateWithLifecycle()
                val context = LocalContext.current

                Spacer(modifier = Modifier.height(15.dp))

                if (listRSSLocal.isNotEmpty()){
                    LazyColumn {
                        items(listRSSLocal){ rss ->
                            RSSLocalCell(rssInfoUI = rss, context = context, removeLocalRSS = viewModel::removeRSS)
                        }
                    }
                }
                else {
                    RSSNotFound()
                }


                TextButton(onClick = viewModel::visibleBSAddRes, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .padding(bottom = 10.dp)) {
                    Row(horizontalArrangement = spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "add rss"
                        )
                        Text(text = "Добавить RSS канал", fontWeight = FontWeight.Normal)
                    }
                }
            }
        }

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        if (viewModel.visibleBSAddRss){
            BSAddRSS(sheetState = sheetState, bsAddRSSImp = viewModel.bsAddRSS)
        }
    }
}

@Composable
private fun RSSLocalCell(rssInfoUI: RSSInfoUI, context: Context, removeLocalRSS: (rss: RSSInfoUI) -> Unit){
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = spacedBy(10.dp), modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)) {

        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(rssInfoUI.image)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = stringResource(id = R.string.imageRSS),
            modifier = Modifier
                .weight(0.8f)
                .aspectRatio(1f),
            content = {
                val painter = painter
                val state = painter.state

                AnimatedContent(
                    state,
                    transitionSpec = {
                        fadeIn(
                            animationSpec = tween(1500)
                        ) togetherWith fadeOut(animationSpec = tween(1500))
                    },
                    label = "Animated Content",
                    modifier = Modifier.fillMaxSize()
                ) { targetState ->
                    when(targetState){
                        is AsyncImagePainter.State.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .shimmerEffect()
                            )
                        }
                        is AsyncImagePainter.State.Success -> {
                            Image(
                                painter = painter,
                                contentDescription = stringResource(id = R.string.imageRSS),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_rss),
                                contentDescription = stringResource(id = R.string.imageRSS),
                                modifier = Modifier.fillMaxSize(),
                                tint = Orange
                            )
                        }
                    }
                }
            }
        )

        Column(modifier = Modifier.weight(4f)) {
            Text(
                text = rssInfoUI.title,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                color = MaterialTheme.colorScheme.onSecondary,
                fontWeight = FontWeight.Bold,
                softWrap = true,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = rssInfoUI.description,
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                softWrap = true,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = { removeLocalRSS(rssInfoUI) }, colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red)) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
        }
    }
}

@Composable
private fun RSSNotFound(){
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 10.dp), horizontalArrangement = spacedBy(15.dp), verticalAlignment = Alignment.CenterVertically){

        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_rss),
            contentDescription = stringResource(id = R.string.imageRSS),
            modifier = Modifier
                .weight(0.4f)
                .aspectRatio(1f),
            tint = Orange
        )

        Text(
            text = "На данный момент вы не добавили ни один RSS-канал.",
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(3f)
        )
    }
}

@PreviewLightDark
@Composable
private fun RSSLocalCellPreview() {
    ZaitsevNewsTheme {
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(10.dp)){

            RSSLocalCell(
                rssInfoUI = RSSInfoUI(
                    id = 4033,
                    title = "conclusionemque 234 234",
                    description = "pe 234234 324 234 234  234 234 234 234234r",
                    ling = "aliquet",
                    image = null
                ), context = LocalContext.current,
                removeLocalRSS = {

                }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun RSSNotFoundPreview() {
    ZaitsevNewsTheme {
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(10.dp)){

            RSSNotFound()
        }
    }
}