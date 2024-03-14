package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.compressed

import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseAdapter
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ItemVideoCompressedBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo

class VideoCompressedAdapter(
    private val context: FragmentActivity,
    private val onMore: (MediaInfo) -> Unit
) :
    BaseAdapter<ItemVideoCompressedBinding, MediaInfo>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemVideoCompressedBinding {
        return ItemVideoCompressedBinding.inflate(inflater, parent, false)
    }

    override fun onBind(binding: ItemVideoCompressedBinding, item: MediaInfo) {
        binding.apply {
            size.text = Formatter.formatFileSize(context, item.size)
            time.text = item.time
            name.text = item.name
            more.setOnClickListener {
                onMore(item)
            }

            Glide.with(context)
                .load(item.path)
                .error(context.getDrawable(R.drawable.logo))
                .into(binding.thumbnail);

        }
    }
}