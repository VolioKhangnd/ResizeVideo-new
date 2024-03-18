package com.video.mini.tools.zip.compress.convert.simple.tiny.utils

object VideoUtils {
    fun calculateFileSize(bVideo: Long, bAudio: Long, timeSeconds: Long) =
        (bVideo + bAudio) * timeSeconds / 8
}