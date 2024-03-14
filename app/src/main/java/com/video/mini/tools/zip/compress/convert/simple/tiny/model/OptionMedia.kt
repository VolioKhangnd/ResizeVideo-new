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
    val isPickMultiple: Boolean = dataOriginal.size > 0,
) : Serializable {
    override fun toString(): String {
        return "OptionMedia(dataOriginal=$dataOriginal, optionCompressType=$optionCompressType, bitrate=$bitrate, frameRate=$frameRate, codec=$codec, fileSize=$fileSize, mimetype=$mimetype, mediaAction=$mediaAction, newResolution=$newResolution, nameOutput=$nameOutput, startTime=$startTime, endTime=$endTime, speed=$speed, preset='$preset', reverseAudio=$reverseAudio, withAudio=$withAudio, isFastVideo=$isFastVideo, isPickMultiple=$isPickMultiple)"
    }
}