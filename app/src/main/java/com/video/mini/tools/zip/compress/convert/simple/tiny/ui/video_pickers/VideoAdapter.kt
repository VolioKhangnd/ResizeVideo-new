package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.video_pickers

import android.content.Context
import android.os.Build
import android.text.format.Formatter
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

interface IOnItemClickListener {
    fun onItemClick(mediaInfo: MediaInfo)
    fun showNotification(isPremium: Boolean = false, message: String)
}

class VideoAdapter(
    private val context: Context,
    private val mediaAction: MediaAction,
    private val isPremium: Boolean,
    private val onItemClick: IOnItemClickListener
) :
    BaseAdapter<ItemVideoBinding, MediaInfo>() {

    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemVideoBinding {
        return ItemVideoBinding.inflate(inflater, parent, false)
    }

    private fun countItemsSelected() = submitData.stream().filter { it.isSelected }.count().toInt()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBind(binding: ItemVideoBinding, item: MediaInfo) {
        binding.apply {
            size.text = Formatter.formatFileSize(context, item.size)
            time.text = item.time

            Glide.with(context)
                .load("file:///"+item.path)
                .error(ContextCompat.getDrawable(context, R.drawable.logo))
                .into(thumbnail)

            setImageChecked(binding, item.isSelected)
            val isCompressOrJoinAction = (mediaAction is MediaAction.CompressVideo || mediaAction is MediaAction.JoinVideo)

            mItem.setOnClickListener {
                val isItemSelected = item.isSelected
                if (!isPremium && countItemsSelected() == 3 && isCompressOrJoinAction && !isItemSelected) {
                    onItemClick.showNotification(isPremium, message = "")
                    return@setOnClickListener
                }
                if (countItemsSelected() == 1 && !(mediaAction is MediaAction.CompressVideo || mediaAction is MediaAction.JoinVideo) && !isItemSelected) {
                    onItemClick.showNotification(message = "")
                    return@setOnClickListener
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