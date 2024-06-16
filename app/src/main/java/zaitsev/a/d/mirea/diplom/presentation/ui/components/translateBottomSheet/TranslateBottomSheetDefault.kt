package zaitsev.a.d.mirea.diplom.presentation.ui.components.translateBottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.data.LanguageModelTranslate
import zaitsev.a.d.mirea.diplom.data.StateExistLanguage
import zaitsev.a.d.mirea.diplom.data.TranslateModelNews
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import zaitsev.a.d.mirea.translateml.ml.Language

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslateBottomSheetDefault(
    stateSheet: SheetState,
    model: TranslateModelNews?,
    toggleLanguage: () -> Unit,
    onSelectTarget: (LanguageModelTranslate) -> Unit,
    onSelectSource: (LanguageModelTranslate) -> Unit,
    deleteModel: (LanguageModelTranslate) -> Unit,
    dismiss: () -> Unit
) {

    ModalBottomSheet(onDismissRequest = dismiss,
        sheetState = stateSheet,
        dragHandle = {
            BottomSheetDefaults.DragHandle()
        }
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)) {

            if (model != null){

                var selectedLanguageTarget by remember {
                    mutableStateOf(true)
                }

                var search by remember {
                    mutableStateOf("")
                }


                Row(modifier = Modifier
                    .fillMaxWidth()) {
                    TextButton(onClick = {
                        selectedLanguageTarget = true
                    }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                        Text(text = model.selectedSourceLanguage.language.toString().uppercase())
                    }
                    IconButton(onClick = {
                        selectedLanguageTarget = true
                        toggleLanguage()
                    }, colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSecondary)) {
                        Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_swap), contentDescription = null)
                    }
                    TextButton(onClick = {
                        selectedLanguageTarget = false
                    }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                        Text(text = model.selectedTargetLanguage?.language?.toString()?.uppercase() ?: stringResource(R.string.selectLanguage))
                    }
                }

                TextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text(stringResource(R.string.searchLanguage)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
                        errorContainerColor = MaterialTheme.colorScheme.onPrimary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(10.dp),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "search")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )


                val selected = if (selectedLanguageTarget) model.selectedTargetLanguage else model.selectedSourceLanguage

                LazyColumn(modifier = Modifier.fillMaxWidth(), state = rememberLazyListState()) {
                    items(model.list.filter {
                            language -> (language != selected) &&
                            (if (search.trim().isNotEmpty())
                                language.language.toString().lowercase().contains(search.lowercase()
                                ) else true)

                    }){ language ->

                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            TextButton(
                                onClick = {
                                    if (selectedLanguageTarget)
                                        onSelectTarget(language)
                                    else onSelectSource(language)
                                },
                                enabled = language.isExist != StateExistLanguage.LOADING,
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSecondary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = language.language.toString(), modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp))
                            }
                            if (language.isExist == StateExistLanguage.EXIST){
                                IconButton(onClick = {
                                    deleteModel(language)
                                }, colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red)) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null
                                    )
                                }
                            }
                            else {
                                if (language.isExist == StateExistLanguage.LOADING){
                                    Box(modifier = Modifier.padding(horizontal = 13.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f),
                                            trackColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.4f),
                                        )
                                    }
                                }
                                else {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_download),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun TranslateBottomSheetDefaultPreview() {
    val sheetState = SheetState(
        skipPartiallyExpanded = false,
        density = LocalDensity.current, initialValue = SheetValue.Expanded,
        confirmValueChange = { true },
        skipHiddenState = false
    )

    var model by remember {
        mutableStateOf(
            TranslateModelNews(
                list = mutableStateListOf<LanguageModelTranslate>().apply {
                    add(LanguageModelTranslate(language = Language("ru"), isExist = StateExistLanguage.EXIST))
                    add(LanguageModelTranslate(language = Language("en"), isExist = StateExistLanguage.EXIST))
                    add(LanguageModelTranslate(language = Language("sq"), isExist = StateExistLanguage.NOT_EXIST))
                    add(LanguageModelTranslate(language = Language("bg"), isExist = StateExistLanguage.LOADING))
                    add(LanguageModelTranslate(language = Language("zh"), isExist = StateExistLanguage.NOT_EXIST))
                    add(LanguageModelTranslate(language = Language("da"), isExist = StateExistLanguage.NOT_EXIST))
                    add(LanguageModelTranslate(language = Language("ja"), isExist = StateExistLanguage.NOT_EXIST))
                },
                selectedTargetLanguage = LanguageModelTranslate(language = Language("en"), isExist = StateExistLanguage.EXIST),
                selectedSourceLanguage = LanguageModelTranslate(language = Language("ru"), isExist = StateExistLanguage.EXIST)
            )
        )
    }
    ZaitsevNewsTheme {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp)){

            TranslateBottomSheetDefault(
                stateSheet = sheetState,
                model = model,
                onSelectTarget = { languageModelTranslate ->
                    if (languageModelTranslate != model.selectedSourceLanguage){
                        model = model.copy(selectedTargetLanguage = languageModelTranslate)
                    }
                },
                onSelectSource = { languageModelTranslate ->
                    if (languageModelTranslate != model.selectedTargetLanguage){
                        model = model.copy(selectedSourceLanguage = languageModelTranslate)
                    }
                },
                toggleLanguage = {
                    model.selectedTargetLanguage?.let { selectedTarget ->
                        model = model.copy(selectedTargetLanguage = model.selectedSourceLanguage, selectedSourceLanguage = selectedTarget)
                    }
                },
                deleteModel = {

                },
                dismiss = {

                })
        }
    }

}