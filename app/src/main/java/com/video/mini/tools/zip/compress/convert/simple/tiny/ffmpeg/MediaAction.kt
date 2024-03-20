package com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg

import java.io.Serializable

sealed class MediaAction(val action: String) : Serializable {
    data object CompressVideo : MediaAction("Compress video") {

    }

    data object CutTrim : MediaAction("CutOrTrim") {
        data object CutVideo  : MediaAction("Cut video")
        data object TrimVideo  : MediaAction("Trim video")
    }

    data object FastForward : MediaAction("Fast Forward")
    data object SlowVideo : MediaAction("Slow Video")
    data object ExtractAudio : MediaAction("Extract Audio")
    data object ReveresVideo : MediaAction("Reveres Video")
    data object JoinVideo : MediaAction("Join Video")
}