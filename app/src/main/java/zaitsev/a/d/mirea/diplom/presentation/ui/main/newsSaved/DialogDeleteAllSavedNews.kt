package zaitsev.a.d.mirea.diplom.presentation.ui.main.newsSaved

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme

@Composable
fun DialogDeleteAllSavedNews(onDismiss: () -> Unit, onDeleteAllLocalNews: () -> Unit) {
    AlertDialog(
        iconContentColor = MaterialTheme.colorScheme.onSecondary,
        titleContentColor = MaterialTheme.colorScheme.onSecondary,
        textContentColor = MaterialTheme.colorScheme.onSecondary,
        icon = {
            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "Example Icon")
        },
        title = {
            Text(text = stringResource(R.string.apply), textAlign = TextAlign.Center)
        },
        text = {
            Text(text = stringResource(R.string.removeAllLocalNews), textAlign = TextAlign.Center)
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDeleteAllLocalNews()
                onDismiss()
            }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun DialogDeleteAllSavedNewsPreview() {
    ZaitsevNewsTheme {
        DialogDeleteAllSavedNews(onDismiss = {}, onDeleteAllLocalNews = {})
    }
}