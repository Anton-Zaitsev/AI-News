package zaitsev.a.d.mirea.diplom.presentation.ui.main.newsSaved

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.data.rss.AstroBeneNews
import zaitsev.a.d.mirea.diplom.data.rss.BBCNews
import zaitsev.a.d.mirea.diplom.data.rss.BankiNews
import zaitsev.a.d.mirea.diplom.data.rss.GoogleNews
import zaitsev.a.d.mirea.diplom.data.rss.MailNews
import zaitsev.a.d.mirea.diplom.data.rss.NewYorkNews
import zaitsev.a.d.mirea.diplom.data.rss.TassNews
import zaitsev.a.d.mirea.diplom.presentation.ui.components.NavigationCenterBarDefault
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsCell.AstroBeneNewsCell
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsCell.BBCNewsCell
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsCell.BankiNewsCell
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsCell.MailNewsCell
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsCell.NewYorkNewsCell
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsCell.TassNewsCell
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsToday.NewsSkeleton
import zaitsev.a.d.mirea.diplom.presentation.ui.mainNavigation.currentRoute

@Composable
fun SavedNews(navHostController: NavHostController, viewModel: SavedNewsViewModel = hiltViewModel()) {

    var openAlertDialogDeleteAll by remember { mutableStateOf(false) }

    NavigationCenterBarDefault(
        navHostController = navHostController,
        title = stringResource(R.string.savedNews),
        contentAction = {
            IconButton(onClick = {
                openAlertDialogDeleteAll = true
            }, colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red)) {
                Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "deleteAllItems")
            }
        },
        content = { paddingValues ->

            val modelNews by viewModel.newsLocal.collectAsStateWithLifecycle()

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)) {

                val context = LocalContext.current

                if (!modelNews.isLoaded){
                    repeat(4){
                        NewsSkeleton(modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp))
                    }
                }
                else {
                    if (modelNews.listNews.isEmpty()){
                        NotFoundSavedNews(navHostController = navHostController)
                    }
                    else {
                        LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)){
                            items(modelNews.listNews){ item ->
                                when(item){
                                    is AstroBeneNews -> {
                                        AstroBeneNewsCell(astroBeneNews = item, context = context, navigateToShare = {
                                            navHostController.navigate(item.currentRoute)
                                        })
                                    }
                                    is BankiNews -> {
                                        BankiNewsCell(bankiNews = item, context = context, navigateToShare = {
                                            navHostController.navigate(item.currentRoute)
                                        })
                                    }
                                    is GoogleNews -> {

                                    }
                                    is MailNews -> {
                                        MailNewsCell(mailNewsData = item, context = context, navigateToShare = {
                                            navHostController.navigate(item.currentRoute)
                                        })
                                    }
                                    is TassNews -> {
                                        TassNewsCell(tassData = item, context = context, navigateToShare = {
                                            navHostController.navigate(item.currentRoute)
                                        })
                                    }
                                    is BBCNews -> {
                                        BBCNewsCell(bbcNews = item, context = context, navigateToShare = {
                                            navHostController.navigate(item.currentRoute)
                                        })
                                    }
                                    is NewYorkNews -> {
                                        NewYorkNewsCell(newYorkTimes = item, context = context, navigateToShare = {
                                            navHostController.navigate(item.currentRoute)
                                        })
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (openAlertDialogDeleteAll){
                DialogDeleteAllSavedNews(onDismiss = {
                    openAlertDialogDeleteAll = false
                }, onDeleteAllLocalNews = viewModel::deleteAll)
            }
        }
    )
}


@Composable
private fun NotFoundSavedNews(navHostController: NavHostController){
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(0.75F),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Absolute.spacedBy(5.dp)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Absolute.spacedBy(5.dp)) {
                Text(
                    text = stringResource(R.string.ops),
                    color = MaterialTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    lineHeight = MaterialTheme.typography.headlineLarge.lineHeight,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.notFoundLocalNews),
                    color = MaterialTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.End,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    lineHeight = MaterialTheme.typography.titleLarge.lineHeight,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.hintNotFoundLocalNews),
                    color = Color.Gray,
                    textAlign = TextAlign.End,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 3.dp)
                )
            }

            val infiniteTransition = rememberInfiniteTransition(label = "animatedIconSave")

            val color by infiniteTransition.animateColor(
                initialValue = MaterialTheme.colorScheme.onSecondary,
                targetValue = Color.Red,
                animationSpec = infiniteRepeatable(
                    animation = tween(2500, easing = LinearOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = "AnimatedColorSave"
            )

            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_heart_fill),
                contentDescription = "Safe Local",
                tint = color
            )
        }

        TextButton(onClick = navHostController::popBackStack, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary), modifier = Modifier.padding(vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Absolute.spacedBy(5.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null
                )
                Text(text = stringResource(R.string.goToNews))
            }
        }
    }
}