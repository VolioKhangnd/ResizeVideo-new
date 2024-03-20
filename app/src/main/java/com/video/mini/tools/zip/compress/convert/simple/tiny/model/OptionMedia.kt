package com.video.mini.tools.zip.compress.convert.simple.tiny.model

import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.MediaAction
import java.io.Serializable

data class OptionMedia(
    val dataOriginal: MediaInfos,
    var optionCompressType: OptionCompressType? = null,
    var bitrate: Long = 0,
    var frameRate: Int = 0,
    val codec: String? = null,
    val fileSize: Long = 0,
    val mimetype: String? = null,
    val mediaAction: MediaAction,
    val newResolution: Resolution = Resolution(),
    var nameOutput: String? = null,
    var startTime: Long = 0,
    var endTime: Long = 0,
    var speed: Float = 1.0f,
    var preset: String = "medium",
    var reverseAudio: Boolean = false,
    var withAudio: Boolean = true,
    var isFastVideo: Boolean = true,
    var isCropVideo: Boolean = false,
    val isPickMultiple: Boolean = dataOriginal.size > 0,
    val cropX: Int = 0,
    val cropY: Int = 0
) : Serializable {

}