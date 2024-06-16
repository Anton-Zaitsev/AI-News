package zaitsev.a.d.mirea.diplom.presentation.ui.main.newsToday

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import zaitsev.a.d.mirea.diplom.presentation.shimmerEffect
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme


@Composable
fun NewsSkeleton(modifier: Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .weight(1.3f)
                .aspectRatio(1F)
                .clip(RoundedCornerShape(15.dp))
                .shimmerEffect()
        )

        Column(modifier = Modifier.weight(5f).padding(horizontal = 10.dp), verticalArrangement = spacedBy(5.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6F)
                    .height(15.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4F)
                    .height(20.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
        }
    }
}

@Preview
@Composable
private fun NewsSkeletonPreview() {
    ZaitsevNewsTheme {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(20.dp)) {
            NewsSkeleton(modifier = Modifier)
        }
    }
}