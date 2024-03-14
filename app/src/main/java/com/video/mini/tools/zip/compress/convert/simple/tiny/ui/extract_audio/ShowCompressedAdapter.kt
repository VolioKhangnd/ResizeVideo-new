package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.extract_audio

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseAdapter
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ItemAudioBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo

class ShowAudioCompressedAdapter(private val context: Context ) :
    BaseAdapter<ItemAudioBinding, MediaInfo>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemAudioBinding {
        return ItemAudioBinding.inflate(inflater, parent, false)
    }

    override fun onBind(binding: ItemAudioBinding, item: MediaInfo) {
        binding.apply {
            date.text = item.date
            time.text = item.time
            name.text = item.name
        }
    }
}