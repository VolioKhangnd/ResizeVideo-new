package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.extract_audio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ExtractViewModel : ViewModel() {
    private val _selectedRadioIndex = MutableLiveData<Int>()
    val selectedRadioIndex: LiveData<Int> = _selectedRadioIndex
    val onClickContinues = MutableLiveData<String>()

    private val mimeAudio = arrayOf("mp3", "aac", "m4a", "wav", "flac", "ogg")

    init {
        _selectedRadioIndex.value = 0
    }

    fun setSelectedRadioIndex(index: Int) {
        _selectedRadioIndex.value = index
    }

    fun setOnClickContinues() {
        onClickContinues.value = mimeAudio[selectedRadioIndex.value!!]
    }
}