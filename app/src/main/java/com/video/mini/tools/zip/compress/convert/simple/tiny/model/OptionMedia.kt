package com.video.mini.tools.zip.compress.convert.simple.tiny.model

import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.MediaAction
import java.io.Serializable

data class MediaOptions(
    var optionCompressType: OptionCompressType? = null,
    var bitrate: Long = 0,
    var frameRate: Int = 0,
    val codec: String? = null
)

data class MediaProperties(
    val fileSize: Long = 0,
    val mimetype: String? = null,
)

data class MediaProcessOptions(
    val newResolution: Resolution = Resolution(),
    var nameOutput: String? = null,
    var startTime: Long = 0,
    var endTime: Long = 0,
    var speed: Float = 1.0f,
    var preset: String = "medium",
    var reverseAudio: Boolean = false,
    var withAudio: Boolean = true,
    var isFastVideo: Boolean = false,
    var isCropVideo: Boolean = false,
    val cropX: Int = 0,
    val cropY: Int = 0
)

data class OptionMedia(
    val dataOriginal: MediaInfos,
    val mediaAction: MediaAction,
    val isPickMultiple: Boolean = dataOriginal.size > 0,
    val mediaOptions: MediaOptions = MediaOptions(),
    val mediaProperties: MediaProperties = MediaProperties(),
    val mediaProcessOptions: MediaProcessOptions = MediaProcessOptions()
) : Serializable
