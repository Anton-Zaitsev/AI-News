package zaitsev.a.d.mirea.diplom.presentation.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import zaitsev.a.d.mirea.diplom.constApp.Constants
import zaitsev.a.d.mirea.diplom.data.ChipRSSChannel
import zaitsev.a.d.mirea.diplom.data.rss.ModelNews
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import kotlin.reflect.KClass

@Composable
fun ListChipChannel(
    modifier: Modifier = Modifier,
    stateList: LazyListState,
    listChip: SnapshotStateList<ChipRSSChannel>,
    onClickClip: (index: Int) -> Unit,
    composeData: @Composable (type: KClass<out ModelNews>) -> Unit,
){
    LazyRow(state = stateList,  modifier = modifier){
        itemsIndexed(listChip){ index, item ->
            FilterChip(
                onClick = {
                    onClickClip(index)
                },
                selected = item.selected,
                label = { Text(text = stringResource(id = item.name)) },
                leadingIcon = {
                    item.icon?.let { icon ->
                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = stringResource(id = item.name),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                modifier = Modifier
                    .height(40.dp)
                    .padding(horizontal = 5.dp),
                shape = RoundedCornerShape(15.dp)
            )
        }
    }

    composeData(listChip.first { it.selected }.type)
}

@PreviewLightDark
@Composable
private fun NavigationGraphPreview() {
    ZaitsevNewsTheme {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
            ListChipChannel(stateList = rememberLazyListState(), listChip = Constants.getListAllNewsRSS(), onClickClip = {}){

            }
        }
    }
}