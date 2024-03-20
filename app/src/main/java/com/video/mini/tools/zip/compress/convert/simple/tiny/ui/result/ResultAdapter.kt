package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.result


import android.content.Intent
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.jaygoo.widget.RangeSeekBar
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseAdapter
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ItemRsBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.video.ShowVideoActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils.loadImage
import java.io.File


interface OnItemMenuMoreSelectedListen {
    fun onRename(item: Pair<MediaInfo, MediaInfo>)
    fun onCompare(item: Pair<MediaInfo, MediaInfo>)
    fun onReplace(item: Pair<MediaInfo, MediaInfo>)
}

class ResultAdapter(
    private val context: FragmentActivity,
    private val sizeBefore: Long = 0,
    private val onCLickOption: OnItemMenuMoreSelectedListen
) :
    BaseAdapter<ItemRsBinding, Pair<MediaInfo, MediaInfo>>() {

    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemRsBinding {
        return ItemRsBinding.inflate(inflater, parent, false)
    }

    private lateinit var binding: ItemRsBinding

    override fun onBind(binding: ItemRsBinding, item: Pair<MediaInfo, MediaInfo>) {
        this.binding = binding
        val mediaBefore = item.first
        val mediaAfter = item.second
        if (mediaAfter.isVideo) binding.resolutionAfter.text = mediaAfter.resolution.toString()
        if (mediaBefore.isVideo) binding.resolutionBefore.text = mediaBefore.resolution.toString()
        val sizeBefore = if (this.sizeBefore != 0L) this.sizeBefore else mediaBefore.size

        hideReplace()

        displayMediaThumbnail(mediaBefore, mediaAfter)
        setSeekBarAndSize(
            maxOf(mediaAfter.size, sizeBefore),
            binding.seekCompress,
            binding.sizeAfter,
            mediaAfter.size.toFloat()
        )
        setSeekBarAndSize(
            maxOf(mediaAfter.size, sizeBefore),
            binding.seekOriginal,
            binding.sizeBefore,
            sizeBefore.toFloat()
        )

        thumbnailClickHandler(mediaAfter)
        moreClickHandler(binding)

        setOptionClickListeners(item)
    }

    private fun hideReplace() {
        binding.option.replace.visibility =
            if (sizeBefore == 0L) View.VISIBLE else View.GONE
        binding.option.compare.visibility =
            if (sizeBefore == 0L) View.VISIBLE else View.GONE
    }

    private fun displayMediaThumbnail(mediaBefore: MediaInfo, mediaAfter: MediaInfo) {
        if (mediaAfter.isVideo) {
            context.loadImage(
                mediaBefore.path,
                binding.thumbnail
            )
        } else {
            binding.icPlay.setImageResource(R.drawable.audio)
        }
        binding.name.text = mediaAfter.name
    }

    private fun thumbnailClickHandler(mediaAfter: MediaInfo) {
        binding.thumbnail.setOnClickListener {
            if (sizeBefore != 0L) {
                val videoFile = File(mediaAfter.path)
                val fileUri = FileProvider.getUriForFile(
                    context,
                    "com.video.mini.tools.zip.compress.convert.simple.tiny",
                    videoFile
                )
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri, "video/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER
                context.startActivity(intent)
                return@setOnClickListener
            }
            val intent = Intent(context, ShowVideoActivity::class.java)
            intent.putExtra("path", mediaAfter.path)
            context.startActivity(intent)
        }
    }

    private fun moreClickHandler(binding: ItemRsBinding) {
        binding.more.setOnClickListener {
            getVisibilityOption(binding).let {
                if (!it) return@let
                val animUtils =
                    AnimationUtils.loadAnimation(context, R.anim.anim_open_option_video_compressed)
                this.binding.option.root.startAnimation(animUtils)
            }
        }
    }

    private fun setOptionClickListeners(item: Pair<MediaInfo, MediaInfo>) {
        val onClick = View.OnClickListener {
            when (it.id) {
                R.id.rename -> onCLickOption.onRename(item)

                R.id.compare -> onCLickOption.onCompare(item)

                R.id.replace -> onCLickOption.onReplace(item)
            }
        }

        binding.option.rename.setOnClickListener(onClick)
        binding.option.compare.setOnClickListener(onClick)
        binding.option.replace.setOnClickListener(onClick)
    }

    private fun getVisibilityOption(binding: ItemRsBinding): Boolean {
        binding.option.root.visibility =
            if (binding.option.root.isVisible) View.GONE else View.VISIBLE
        return binding.option.root.isVisible
    }

    private fun setSeekBarAndSize(
        maxProgress: Long,
        seekBar: RangeSeekBar,
        sizeTextView: TextView,
        size: Float
    ) {
        seekBar.isEnabled = false
        seekBar.setRange(0f, maxProgress.toFloat())
        seekBar.setProgress(size)
        sizeTextView.text = Formatter.formatFileSize(context, size.toLong())
    }
}
