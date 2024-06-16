package zaitsev.a.d.mirea.telegramapi.api

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi

internal sealed class ResultDownload {
    internal data object Nothing: ResultDownload()
    internal data class Success(val file: TdApi.File): ResultDownload()
    internal data class Error(val code: Int? = null, val error: String = "Не удалось скачать файл"): ResultDownload()
}

internal suspend fun TdApi.MessageAnimatedEmoji.download(client: Client): ResultDownload {
    val sticker = animatedEmoji.sticker.sticker
    return if (sticker != null && !sticker.local.isDownloadingCompleted && !sticker.local.isDownloadingActive && sticker.local.canBeDownloaded){
        getCallbackDownloadByID(client = client, id = sticker.remote.id).firstOrNull() ?: ResultDownload.Nothing
    }else ResultDownload.Nothing
}

internal suspend fun TdApi.MessageVideo.download(client: Client): ResultDownload{
    val video = this.video.video
    return if (video != null && !video.local.isDownloadingCompleted && !video.local.isDownloadingActive && video.local.canBeDownloaded) {
        getCallbackDownloadByID(client = client, id = video.remote.id).firstOrNull() ?: ResultDownload.Nothing
    }
    else ResultDownload.Nothing
}

internal suspend fun TdApi.MessagePhoto.download(client: Client): ResultDownload{
    val photo = photo?.sizes?.getOrNull(1)?.photo
    return if (photo != null && !photo.local.isDownloadingCompleted && !photo.local.isDownloadingActive && photo.local.canBeDownloaded) {
        getCallbackDownloadByID(client = client, id = photo.remote.id).firstOrNull() ?: ResultDownload.Nothing
    }
    else ResultDownload.Nothing
}


internal suspend fun TdApi.MessageSticker.download(client: Client): ResultDownload{
    val sticker = sticker.sticker
    return if (sticker != null && sticker.local.path.isEmpty() && !sticker.local.isDownloadingActive && sticker.local.canBeDownloaded) {
        getCallbackDownloadByID(client = client, id = sticker.remote.id).firstOrNull() ?: ResultDownload.Nothing
    }
    else ResultDownload.Nothing
}


private fun getCallbackDownloadByID(client: Client, id: String): Flow<ResultDownload> = callbackFlow {
    client.send(TdApi.GetRemoteFile(id, null)) { obj ->
        when (obj.constructor) {
            TdApi.Error.CONSTRUCTOR -> {
                val error = obj as TdApi.Error
                val code = error.code
                if (code != TelegramHelper.IGNORED_ERROR_CODE) {
                    trySend(ResultDownload.Error(code = code, error = error.message))
                }
                else {
                    trySend(ResultDownload.Error())
                }
            }
            TdApi.File.CONSTRUCTOR -> {
                val file = obj as TdApi.File
                client.send(TdApi.DownloadFile(file.id, 10, 0, 0, true)) { fileDownloaded ->
                    if (fileDownloaded !is TdApi.File) {
                        trySend(ResultDownload.Error())
                    }
                    else {
                        trySend(ResultDownload.Success(fileDownloaded))
                    }
                }
            }
        }
    }
    awaitClose {
        close()
    }
}
