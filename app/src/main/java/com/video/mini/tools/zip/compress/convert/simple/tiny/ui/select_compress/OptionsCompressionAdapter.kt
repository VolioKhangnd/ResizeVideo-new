package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.select_compress

import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseAdapter
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ItemOptionCompressionBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.OptionCompression
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.VideoUtils


class OptionsCompressionAdapter() :
    BaseAdapter<ItemOptionCompressionBinding, OptionCompression>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemOptionCompressionBinding {
        return ItemOptionCompressionBinding.inflate(inflater, parent, false)
    }

    override fun onBind(binding: ItemOptionCompressionBinding, item: OptionCompression) {
        binding.apply {
            val detail = item.detail
            title = item.title
            description = if (detail.isNotEmpty()) item.description + " - " + detail else item.description
            isSelected = item.isSelected
        }
    }
}