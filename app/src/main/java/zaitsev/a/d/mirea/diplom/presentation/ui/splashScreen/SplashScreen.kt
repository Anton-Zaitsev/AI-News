package zaitsev.a.d.mirea.diplom.presentation.ui.splashScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import zaitsev.a.d.mirea.diplom.R

@Composable
fun SplashScreen(
    navController: NavController,
    mainScreen: String,
    splashScreen: String,
    loginScreen: String,
    viewModel: SplashViewModel = hiltViewModel()
) {
    Column(
        modifier =  Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = stringResource(id = R.string.app_name),
            modifier = Modifier.fillMaxWidth(0.3F).aspectRatio(1F)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(id = R.string.app_name),
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
    LaunchedEffect(Unit) {
        val resultIsExistUser = viewModel.checkExistUser()
        val navigateScreen = if (resultIsExistUser) mainScreen else loginScreen
        delay(1500)

        withContext(Dispatchers.Main){
            navController.navigate(navigateScreen){
                popUpTo(splashScreen) {
                    inclusive = true
                }
            }
        }
    }
}