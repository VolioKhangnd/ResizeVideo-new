package com.tearas.resizevideo.ffmpeg

import com.tearas.resizevideo.model.MediaInfo

interface IProcessFFmpeg {
    fun processElement(currentElement: Int, percentage: Int)
    fun onCurrentElement(position: Int) {}
    fun onSuccess(currentIndex: Int, mediaInfo: MediaInfo) {}
    fun onFailure(currentIndex: Int, error: String) {}
    fun onFinish() {}
    fun result(elementSuccess: Int, elementFailure: Int) {}
}