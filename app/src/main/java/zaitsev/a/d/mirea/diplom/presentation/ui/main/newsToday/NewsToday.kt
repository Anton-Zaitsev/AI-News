package zaitsev.a.d.mirea.diplom.presentation.ui.main.newsToday

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.data.mappers.rssMappers.toCategory
import zaitsev.a.d.mirea.diplom.data.rss.AstroBeneNews
import zaitsev.a.d.mirea.diplom.data.rss.BBCNews
import zaitsev.a.d.mirea.diplom.data.rss.BankiNews
import zaitsev.a.d.mirea.diplom.data.rss.GoogleNews
import zaitsev.a.d.mirea.diplom.data.rss.MailNews
import zaitsev.a.d.mirea.diplom.data.rss.NewYorkNews
import zaitsev.a.d.mirea.diplom.data.rss.TassNews
import zaitsev.a.d.mirea.diplom.presentation.ui.main.ListChipChannel
import zaitsev.a.d.mirea.diplom.presentation.ui.main.ListChipCustomNews
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsCell.AstroBeneNewsCell
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsCell.BBCNewsCell
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsCell.BankiNewsCell
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsCell.MailNewsCell
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsCell.NewYorkNewsCell
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsCell.TassNewsCell
import zaitsev.a.d.mirea.diplom.presentation.ui.mainNavigation.currentRoute
import zaitsev.a.d.mirea.domain.models.newsTypes.BBCType
import zaitsev.a.d.mirea.domain.models.newsTypes.NewYorkType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsToday(navController: NavController, viewModel: NewTodayViewModel = hiltViewModel()){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.newsToday), color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold)
                },
                actions = {
                    SavedNewsButton(navController = navController, savedNewsCount = viewModel.savedNews)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        }, content = { paddingValues ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {

                ListChipChannel(
                    stateList = rememberLazyListState(),
                    listChip = viewModel.listChip,
                    onClickClip = viewModel::onClickClip,
                    modifier = Modifier.padding(vertical = 10.dp)
                ){ selectedNews ->

                    when(selectedNews){
                        BBCNews::class -> {
                            val painter = painterResource(id = R.drawable.bbcnewsrss)
                            ListChipCustomNews(
                                modifier = Modifier.padding(10.dp),
                                painter = painter,
                                selectedBBC = viewModel.selectedBBC ?: BBCType.WORLD,
                                listCustomNews = BBCType.entries,
                                onSelectBBCNews = viewModel::onSelectBBCNews,
                                convertToCategory = { type ->
                                    type.toCategory()
                                }
                            )
                        }
                        NewYorkNews::class -> {
                            val painter = painterResource(id = R.drawable.newyorknewsrss)
                            ListChipCustomNews(
                                modifier = Modifier.padding(10.dp),
                                painter = painter,
                                selectedBBC = viewModel.selectedNY ?: NewYorkType.WORLD,
                                listCustomNews = NewYorkType.entries,
                                onSelectBBCNews = viewModel::onSelectNewYorkNews,
                                convertToCategory = { type ->
                                    type.toCategory()
                                }
                            )
                        }
                        else -> Unit
                    }

                    if (!viewModel.news.isLoaded){
                        repeat(4){
                            NewsSkeleton(modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp))
                        }
                    }
                    else{
                        val context = LocalContext.current

                        LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)){
                            items(viewModel.news.listNews){ item ->
                                when(item){
                                    is AstroBeneNews -> {
                                        AstroBeneNewsCell(astroBeneNews = item, context = context, navigateToShare = {
                                            navController.navigate(item.currentRoute)
                                        })
                                    }
                                    is BankiNews -> {
                                        BankiNewsCell(bankiNews = item, context = context, navigateToShare = {
                                            navController.navigate(item.currentRoute)
                                        })
                                    }
                                    is GoogleNews -> {

                                    }
                                    is MailNews -> {
                                        MailNewsCell(mailNewsData = item, context = context, navigateToShare = {
                                            navController.navigate(item.currentRoute)
                                        })
                                    }
                                    is TassNews -> {
                                        TassNewsCell(tassData = item, context = context, navigateToShare = {
                                            navController.navigate(item.currentRoute)
                                        })
                                    }
                                    is BBCNews -> {
                                        BBCNewsCell(bbcNews = item, context = context, navigateToShare = {
                                            navController.navigate(item.currentRoute)
                                        })
                                    }
                                    is NewYorkNews -> {
                                        NewYorkNewsCell(newYorkTimes = item, context = context, navigateToShare = {
                                            navController.navigate(item.currentRoute)
                                        })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}