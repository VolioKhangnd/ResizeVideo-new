package com.tearas.resizevideo.ui.process

import android.content.Context
import android.graphics.Color
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.tearas.resizevideo.R
import com.tearas.resizevideo.core.BaseAdapter
import com.tearas.resizevideo.databinding.ItemProcessVideoBinding
import com.tearas.resizevideo.databinding.ItemVideoCompressingBinding
import com.tearas.resizevideo.databinding.ItemVideoCompressionBinding
import com.tearas.resizevideo.model.MediaInfo
import com.tearas.resizevideo.model.StateCompression

class ProcessAdapter(val context: Context) : BaseAdapter<ItemVideoCompressingBinding, MediaInfo>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemVideoCompressingBinding {
        return ItemVideoCompressingBinding.inflate(inflater, parent, false)
    }

    override fun onBind(binding: ItemVideoCompressingBinding, item: MediaInfo) {
        binding.apply {
            size.text = Formatter.formatFileSize(context, item.size)
            Glide.with(context)
                .load("file:///" + item.path)
                .error(context.getDrawable(R.drawable.img)!!.setTint(Color.GRAY))
                .into(binding.thumbnail);
            val drawState = when (item.stateCompression) {
                StateCompression.Processing -> R.drawable.ic_compress_video
                StateCompression.Waiting -> R.drawable.baseline_access_time_24
                StateCompression.Failure -> R.drawable.ic_close_24
                StateCompression.Success -> R.drawable.baseline_done_24
            }
            state.setImageDrawable(context.getDrawable(drawState)!!)
        }
    }
}