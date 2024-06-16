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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramContent
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramMessageModelUI
import zaitsev.a.d.mirea.diplom.presentation.getDateTimeNow
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import zaitsev.a.d.mirea.diplom.presentation.theme.getBGColorChatInc
import zaitsev.a.d.mirea.diplom.presentation.theme.getBGColorChatOut
import zaitsev.a.d.mirea.diplom.presentation.theme.getDateColorChat
import zaitsev.a.d.mirea.diplom.presentation.toTimeChat
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.chatComponent.AvatarMessage
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.getReadingStatusIcon
@Composable
fun UnsupportedMessageUI(model: TelegramMessageModelUI, darkTheme: Boolean) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
        horizontalArrangement = if (model.isOutgoing) Arrangement.End else Arrangement.Start
    ) {

        if (model.isOutgoing){
            Spacer(modifier = Modifier.weight(1f, fill = true))
        }

        if (model.userSender != null){
            Row(modifier = Modifier.weight(3f, fill = false),
                horizontalArrangement = Arrangement.Absolute.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                if (!model.isOutgoing){
                    AvatarMessage(telegramUserModelUI = model.userSender)
                }
                UnsupportedMessageContent(model, darkTheme, modifier = Modifier)
                if (model.isOutgoing){
                    AvatarMessage(telegramUserModelUI = model.userSender)
                }
            }
        }
        else {
            UnsupportedMessageContent(model, darkTheme, modifier = Modifier.weight(3f, fill = false))
        }

        if (!model.isOutgoing){
            Spacer(modifier = Modifier.weight(1f, fill = true))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UnsupportedMessageContent(model: TelegramMessageModelUI, darkTheme: Boolean, modifier: Modifier){
    FlowRow(modifier = modifier
        .background(
            if (model.isOutgoing)
                getBGColorChatOut(darkTheme)
            else
                getBGColorChatInc(darkTheme), RoundedCornerShape(20.dp)
        )
        .padding(horizontal = 10.dp, vertical = 3.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalArrangement = Arrangement.End,

        ) {

        Row(modifier = Modifier.padding(vertical = 5.dp, horizontal = 2.dp), horizontalArrangement = Arrangement.Absolute.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically){
            Icon(
                imageVector = Icons.Default.Block,
                contentDescription = stringResource(R.string.not_support_message),
                tint = Color.Red
            )
            Text(
                text = stringResource(R.string.not_support_message),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 15.sp
                )
            )
        }

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

@Preview
@Composable
private fun UnsupportedMessageUIPreview() {
    ZaitsevNewsTheme {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp)) {

            val message =  TelegramMessageModelUI(
                id = 6738,
                isOutgoing = true,
                date = getDateTimeNow(),
                editDate = getDateTimeNow(),
                replyMessageID = 3025,
                sendingState = null,
                messageContent = TelegramContent.TelegramTextUI(""),
                isUnread = false,
                canGetViewers = false,
                isChannelPost = false
            )
            UnsupportedMessageUI(model = message, darkTheme = isSystemInDarkTheme())
        }
    }
}