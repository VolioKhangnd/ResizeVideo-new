package com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg

import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo

interface IProcessFFmpeg {
    fun processElement(currentElement: Int, percentage: Int)
    fun onCurrentElement(position: Int) {}
    fun onSuccess(currentIndex: Int, mediaInfo: MediaInfo) {}
    fun onFailure(currentIndex: Int, error: String) {}
    fun onFinish() {}
    fun result(elementSuccess: Int, elementFailure: Int) {}
}