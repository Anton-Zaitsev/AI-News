package zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.messageBox

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramContent
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramMessageModelUI
import zaitsev.a.d.mirea.diplom.presentation.SymbolAnnotationType
import zaitsev.a.d.mirea.diplom.presentation.getDateTimeNow
import zaitsev.a.d.mirea.diplom.presentation.messageFormatter
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import zaitsev.a.d.mirea.diplom.presentation.theme.getBGColorChatInc
import zaitsev.a.d.mirea.diplom.presentation.theme.getBGColorChatOut
import zaitsev.a.d.mirea.diplom.presentation.theme.getDateColorChat
import zaitsev.a.d.mirea.diplom.presentation.toTimeChat
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.chatComponent.AvatarMessage
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.getReadingStatusIcon

@Composable
fun TextMessageUI(model: TelegramMessageModelUI, darkTheme: Boolean) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
        horizontalArrangement = if (model.isOutgoing) Arrangement.End else Arrangement.Start
    ) {

        val color = if (!model.isOutgoing) getBGColorChatInc(darkTheme) else getBGColorChatOut(darkTheme)


        if (model.isOutgoing){
            Spacer(modifier = Modifier.weight(1f, fill = true))
        }

        if (model.userSender != null){
            Row(modifier = Modifier
                .weight(4.5f, fill = false),
                horizontalArrangement = Arrangement.Absolute.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {

                if (!model.isOutgoing){
                    AvatarMessage(telegramUserModelUI = model.userSender)
                }

                TextMessageContent(
                    model = model, darkTheme = darkTheme, modifier = Modifier
                        .background(color, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                )

                if (model.isOutgoing){
                    AvatarMessage(telegramUserModelUI = model.userSender)
                }
            }
        }
        else {
            TextMessageContent(
                model = model, darkTheme = darkTheme, modifier = Modifier
                    .weight(4f, fill = false)
                    .background(color, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }


        if (!model.isOutgoing){
            Spacer(modifier = Modifier.weight(1f, fill = true))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TextMessageContent(
    model: TelegramMessageModelUI,
    darkTheme: Boolean,
    modifier: Modifier
){

    val text = (model.messageContent as TelegramContent.TelegramTextUI).text

    FlowRow(modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
        horizontalArrangement = Arrangement.End,

        ) {

        val uriHandler = LocalUriHandler.current

        val styledMessage = messageFormatter(
            text = text,
            primary = true
        )


        ClickableText(
            text = styledMessage,
            style = TextStyle(
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 15.sp
            ),
            onClick = {
                styledMessage
                    .getStringAnnotations(start = it, end = it)
                    .firstOrNull()
                    ?.let { annotation ->
                        when (annotation.tag) {
                            SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                            SymbolAnnotationType.PERSON.name -> Unit
                            else -> Unit
                        }
                    }
            },
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 2.dp)
        )

        Row(modifier = Modifier
            .padding(start = 10.dp)
            .align(Alignment.Bottom), verticalAlignment = Alignment.CenterVertically) {

            val color = if (model.isOutgoing) getDateColorChat(darkTheme) else Color.Gray

            Text(
                text = model.date.toTimeChat(),
                color = color,
                fontSize = 11.sp,
            )
            if (!(model.isChannelPost || !model.isOutgoing)){
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                    imageVector = model.getReadingStatusIcon(),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = color
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun TextMessageUIPreview(
    @PreviewParameter(TextMessagePreviewParameter::class) message: TelegramMessageModelUI
) {
    ZaitsevNewsTheme {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp)) {
            TextMessageUI(model = message, darkTheme = isSystemInDarkTheme())
        }
    }
}

private class TextMessagePreviewParameter : PreviewParameterProvider<TelegramMessageModelUI> {
    override val values = sequenceOf(
        TelegramMessageModelUI(
            id = 6738,
            isOutgoing = false,
            date = getDateTimeNow(),
            editDate = getDateTimeNow(),
            replyMessageID = 3025,
            sendingState = null,
            messageContent = TelegramContent.TelegramTextUI("мой"),
            isUnread = false,
            canGetViewers = false,
            isChannelPost = false
        ),

        TelegramMessageModelUI(
            id = 6738,
            isOutgoing = true,
            date = getDateTimeNow(),
            editDate = getDateTimeNow(),
            replyMessageID = 3025,
            sendingState = null,
            messageContent = TelegramContent.TelegramTextUI("мой"),
            isUnread = false,
            canGetViewers = false,
            isChannelPost = false
        ),

        TelegramMessageModelUI(
            id = 6738,
            isOutgoing = true,
            date = getDateTimeNow(),
            editDate = getDateTimeNow(),
            replyMessageID = 3025,
            sendingState = null,
            messageContent = TelegramContent.TelegramTextUI("мой"),
            isUnread = true,
            canGetViewers = false,
            isChannelPost = false
        )
    )
}