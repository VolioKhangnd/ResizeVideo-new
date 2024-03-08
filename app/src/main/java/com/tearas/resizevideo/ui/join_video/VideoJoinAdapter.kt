package com.tearas.resizevideo.ui.join_video

import android.content.Intent
import android.graphics.Color
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.tearas.resizevideo.R
import com.tearas.resizevideo.core.BaseAdapter
import com.tearas.resizevideo.databinding.ItemVideoJoinBinding
import com.tearas.resizevideo.model.MediaInfo
import com.tearas.resizevideo.ui.video.ShowVideoActivity

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
                .load("file:///" + item.path)
                .error(root.context.getDrawable(R.drawable.img)!!.setTint(Color.GRAY))
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