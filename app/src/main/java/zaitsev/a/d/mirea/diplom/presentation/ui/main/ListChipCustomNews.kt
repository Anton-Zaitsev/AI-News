package zaitsev.a.d.mirea.diplom.presentation.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp


@Composable
fun <T> ListChipCustomNews(
    modifier: Modifier = Modifier,
    painter: Painter,
    listCustomNews: List<T>,
    selectedBBC: T,
    onSelectBBCNews: (type: T) -> Unit,
    convertToCategory: (type: T) -> String
) {

    val listBBC by remember (listCustomNews) {
        mutableStateOf(listCustomNews)
    }

    LazyRow(state = rememberLazyListState(), modifier = modifier){
        items(listBBC){ type ->
            FilterChip(
                onClick = {
                    onSelectBBCNews(type)
                },
                selected = type == selectedBBC,
                label = { Text(text = convertToCategory(type)) },
                leadingIcon = {
                    Image(
                        painter = painter,
                        contentDescription = convertToCategory(type),
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier
                    .height(40.dp)
                    .padding(horizontal = 5.dp),
                shape = RoundedCornerShape(15.dp)
            )
        }
    }
}