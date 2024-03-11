package com.tearas.resizevideo.utils;

import android.content.Intent;
import android.os.Build;
import android.util.JsonReader
import com.google.gson.Gson;
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonWriter
import com.tearas.resizevideo.ffmpeg.MediaAction;
import com.tearas.resizevideo.model.MediaInfo
import com.tearas.resizevideo.model.MediaInfos
import com.tearas.resizevideo.model.OptionMedia;
import com.tearas.resizevideo.model.StateCompression
import java.io.Serializable

object IntentUtils {

    private const val MEDIA = "media";
    private const val OPTION = "option_media";
    private const val ACTION = "action";
    private const val MEDIA_OUTPUT = "MEDIA_OUTPUT";
    private const val MEDIA_INPUT = "MEDIA_INPUT";

    fun Intent.getActionMedia() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(ACTION, MediaAction::class.java)
    } else {
        getSerializableExtra(ACTION) as MediaAction
    }

    fun Intent.getMediaOutput() = getSerializableExtra(MEDIA_OUTPUT)!! as List<MediaInfo>

    fun Intent.getMediaInput() = getSerializableExtra(MEDIA_INPUT)!! as List<MediaInfo>


    fun Intent.getOptionMedia() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(OPTION, OptionMedia::class.java)
    } else {
        getSerializableExtra(OPTION) as OptionMedia
    }

    fun Intent.passActionMedia(action: MediaAction) = putExtra(ACTION, action)

    fun Intent.passMediaOutput(mediaOutput: List<MediaInfo>) =
        putExtra(MEDIA_OUTPUT, mediaOutput as Serializable)


    fun Intent.passMediaInput(mediaInput: List<MediaInfo>) =
        putExtra(MEDIA_INPUT, mediaInput as Serializable)

    fun Intent.passOptionMedia(
        optionMedia: OptionMedia
    ) = putExtra(OPTION, optionMedia)
}
