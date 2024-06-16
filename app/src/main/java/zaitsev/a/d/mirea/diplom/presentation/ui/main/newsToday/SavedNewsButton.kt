package zaitsev.a.d.mirea.diplom.presentation.ui.main.newsToday

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.presentation.ui.mainNavigation.Screen

@Composable
fun SavedNewsButton(navController: NavController, savedNewsCount: Int) {
    if (savedNewsCount > 0){
        Button(
            onClick = {
                navController.navigate(Screen.NewsSaved.route)
            },
            modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.onSecondary),
            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.Absolute.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_saved),
                    contentDescription = "saved news"
                )
                Text(
                    text = savedNewsCount.toString(),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 10.sp,
                    modifier = Modifier
                        .background(Color.Red, CircleShape)
                        .padding(horizontal = 3.dp)
                )
            }
        }
    }
    else {
        IconButton(onClick = {
            navController.navigate(Screen.NewsSaved.route)
        }, colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSecondary)) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_saved),
                contentDescription = "saved news"
            )
        }
    }
}