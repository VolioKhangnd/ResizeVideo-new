package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.cut_trim

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseAdapter
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ItemFramRateBinding


class FrameRateVideoAdapter(private val context: Context) :
    BaseAdapter<ItemFramRateBinding, Bitmap>() {
    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemFramRateBinding {
        return ItemFramRateBinding.inflate(inflater, parent, false)
    }

    override fun onBind(binding: ItemFramRateBinding, item: Bitmap) {
        binding.img.setImageBitmap(item)
    }
}