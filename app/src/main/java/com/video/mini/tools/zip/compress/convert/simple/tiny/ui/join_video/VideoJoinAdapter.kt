package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.join_video

import android.content.Intent
import android.graphics.Color
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseAdapter
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ItemVideoJoinBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.video.ShowVideoActivity

class VideoJoinAdapter(private val onDelete: (MediaInfo) -> Unit) :
    BaseAdapter<ItemVideoJoinBinding, MediaInfo>() {
    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemVideoJoinBinding {
        return ItemVideoJoinBinding.inflate(inflater, parent, false)
    }

    override fun onBind(binding: ItemVideoJoinBinding, item: MediaInfo) {
        binding.apply {
            size.text = Formatter.formatFileSize(root.context, item.size)
            name.text = item.name
            time.text = item.time
            resolution.text = item.resolution.toString()
            Glide.with(root.context)
                .load( item.path)
                .error(root.context.getDrawable(R.drawable.logo)!!.setTint(Color.GRAY))
                .into(binding.thumbnail);

            play.setOnClickListener {
                val intent = Intent(root.context, ShowVideoActivity::class.java)
                intent.putExtra("path", item.path)
                root.context.startActivity(intent)
            }

            delete.setOnClickListener {
                onDelete(item)
            }
        }
    }

}