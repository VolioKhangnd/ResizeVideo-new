package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.select_compress


import android.content.Context
import android.graphics.Color
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseAdapter
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ItemVideoCompressionBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo

class VideoCompressAdapter(private val context: Context) : BaseAdapter<ItemVideoCompressionBinding, MediaInfo>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemVideoCompressionBinding {
         return ItemVideoCompressionBinding.inflate(inflater, parent, false)
    }

    override fun onBind(binding: ItemVideoCompressionBinding, item: MediaInfo) {
       binding.apply {
           size.text= Formatter.formatFileSize(context,item.size)
           name.text=item.name
           time.text=item.time
           resolution.text=item.resolution.toString()
           Glide.with(context)
               .load("file:///" + item.path)
               .error(context.getDrawable(R.drawable.logo)!!.setTint(Color.GRAY))
               .into(binding.thumbnail);
       }
    }
}