package com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg

import java.io.Serializable

sealed class MediaAction(val action: String) : Serializable {
    data object CompressVideo : MediaAction("Compress video") {

    }

    data object CutTrimCrop : MediaAction("CutOrTrim") {
        data class CutVideo(val isCrop: Boolean = false) : MediaAction("Cut video")
        data class TrimVideo(val isCrop: Boolean = false) : MediaAction("Trim video")
    }

    data object FastForward : MediaAction("Fast Forward")
    data object SlowVideo : MediaAction("Slow Video")
    data object ExtractAudio : MediaAction("Extract Audio")
    data object ReveresVideo : MediaAction("Reveres Video")
    data object JoinVideo : MediaAction("Join Video")
}