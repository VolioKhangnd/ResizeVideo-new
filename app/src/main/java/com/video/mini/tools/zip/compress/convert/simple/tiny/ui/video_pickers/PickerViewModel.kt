package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.video_pickers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfos

class PickerViewModel : ViewModel() {
    val videosLiveData = MutableLiveData<MediaInfo>()
    val closeLiveData = MutableLiveData<Boolean>()
    val actionSort = MutableLiveData<String>()
    val videos = MediaInfos()
    var sumSizeVideos = 0L
    fun size() = videos.size
    fun insertVideo( info: MediaInfo) {

        val index = videos.indexOfLast { info.id == it.id }
        if (info.isSelected) {
            videos.add(info)
        } else {
            if (index != -1) videos.removeAt(index)
        }
        sumSizeVideos = videos.sumOf { it.size }
        videosLiveData.postValue(info)
    }
}