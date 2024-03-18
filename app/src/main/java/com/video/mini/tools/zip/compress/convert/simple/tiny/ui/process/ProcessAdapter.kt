package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.process

import android.content.Context
import android.graphics.Color
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseAdapter
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ItemVideoCompressingBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.StateCompression
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.AnimUtils

class ProcessAdapter(val context: Context) : BaseAdapter<ItemVideoCompressingBinding, MediaInfo>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemVideoCompressingBinding {
        return ItemVideoCompressingBinding.inflate(inflater, parent, false)
    }

    override fun onBind(binding: ItemVideoCompressingBinding, item: MediaInfo) {
        binding.apply {
            name.text = item.name
            size.text = Formatter.formatFileSize(context, item.size)
            Glide.with(context)
                .load(item.path)
                .error(context.getDrawable(R.drawable.logo)!!.setTint(Color.GRAY))
                .into(binding.thumbnail);
            val drawState = when (item.stateCompression) {
                StateCompression.Processing -> R.drawable.ic_compress_video
                StateCompression.Waiting -> R.drawable.baseline_access_time_24
                StateCompression.Failure -> R.drawable.ic_close_24
                StateCompression.Success -> R.drawable.baseline_done_24
            }
            state.setImageResource(drawState)
            val animUtils = android.view.animation.AnimationUtils.loadAnimation(
                context,
                R.anim.anim_zoom_in_out
            )
            state.startAnimation(animUtils)
        }
    }
}