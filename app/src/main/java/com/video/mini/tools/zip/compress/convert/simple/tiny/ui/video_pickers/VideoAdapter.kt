package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.video_pickers

import android.content.Context
import android.os.Build
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseAdapter
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ItemVideoBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.MediaAction
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfos
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.VibrateUtils

interface IOnItemClickListener {
    fun onItemClick(mediaInfo: MediaInfo)
    fun showNotification(isPremium: Boolean = false, message: String)
}

class VideoAdapter(
    private val context: Context,
    private val videosSelected: MediaInfos,
    private val mediaAction: MediaAction,
    private val isPremium: Boolean,
    private val onItemClick: IOnItemClickListener
) :
    BaseAdapter<ItemVideoBinding, MediaInfo>() {

    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemVideoBinding {
        return ItemVideoBinding.inflate(inflater, parent, false)
    }

    private val countItemsSelected get() = videosSelected.size

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBind(binding: ItemVideoBinding, item: MediaInfo) {
        binding.apply {
            size.text = Formatter.formatFileSize(context, item.size)
            time.text = item.time
            Log.d("sdadsadads", item.id.toString())
            Glide.with(context)
                .load("file:///" + item.path)
                .error(ContextCompat.getDrawable(context, R.drawable.logo))
                .into(thumbnail)

            setImageChecked(binding, item.isSelected)

            mItem.setOnClickListener {
                val isItemSelected = item.isSelected
                if (isItemSelected.not()) {
                    VibrateUtils.vibrate(it.context, 50)
                    if (mediaAction is MediaAction.FastForward && countItemsSelected == 1) return@setOnClickListener
                    if (mediaAction is MediaAction.SlowVideo && countItemsSelected == 1) return@setOnClickListener
                    if (mediaAction is MediaAction.CutTrim && countItemsSelected == 1) return@setOnClickListener
                    if (!isPremium) {
                        when {
                            mediaAction is MediaAction.JoinVideo && countItemsSelected == 2 -> return@setOnClickListener
                            mediaAction is MediaAction.CompressVideo && countItemsSelected == 1 -> return@setOnClickListener
                            mediaAction is MediaAction.ExtractAudio && countItemsSelected == 1 -> return@setOnClickListener
                        }
                    }
                }
                val animationResId = if (isItemSelected) R.anim.scale_up else R.anim.scale_down
                val scaleAnimation = AnimationUtils.loadAnimation(context, animationResId)
                mItem.startAnimation(scaleAnimation)
                item.isSelected = !isItemSelected
                setImageChecked(binding, item.isSelected)
                onItemClick.onItemClick(item)
            }
        }
    }


    private fun setImageChecked(binding: ItemVideoBinding, selected: Boolean) {
        binding.imgCheck.setImageResource(if (selected) R.drawable.ic_checked else R.drawable.unchecked)
    }

}