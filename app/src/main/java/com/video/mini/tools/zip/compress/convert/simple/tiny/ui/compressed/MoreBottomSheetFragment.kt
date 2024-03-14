package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.compressed

import android.annotation.SuppressLint
import android.content.Intent
import android.text.format.Formatter
import android.view.View
import com.bumptech.glide.Glide
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseBottomSheetFragment
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.FragmentBottomSheetBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.compare.CompareActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.main.MainActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.result.ResultActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.video.ShowVideoActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.HandleMediaVideo
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.HandleSaveResult

import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passMediaInput
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passMediaOutput
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils.share

class MoreBottomSheetFragment(private val mediaInfo: MediaInfo, private val onDelete: () -> Unit) :
    BaseBottomSheetFragment<FragmentBottomSheetBinding>(R.layout.fragment_bottom_sheet) {
    override fun getViewBinding(view: View): FragmentBottomSheetBinding {
        return FragmentBottomSheetBinding.bind(view)
    }

    override fun initView() {
        setData()
        binding.apply {

            val handleMediaVideo = HandleMediaVideo(requireActivity())
            val handleSaveResult = HandleSaveResult(requireActivity())
            val pathInput = handleSaveResult.getPathInput(mediaInfo.path)
            val intent = Intent(requireActivity(), ResultActivity::class.java)
            val mediaInput = handleMediaVideo.getVideoByPath(pathInput)


            play.setOnClickListener {
                val intent = Intent(requireActivity(), ShowVideoActivity::class.java)
                startActivity(intent.apply {
                    putExtra("path", mediaInfo.path)
                })
            }

            result.setOnClickListener {
                if (mediaInput != null) {
                    startActivity(intent.apply {
                        intent.passMediaOutput(listOf(mediaInfo))
                        intent.passMediaInput(listOf(mediaInput))
                    })
                } else {
                    (requireActivity() as MainActivity).showMessage("Video original not found")
                }
            }

            compare.setOnClickListener {
                if (mediaInput != null) startCompareActivity(Pair(mediaInput, mediaInfo)) else {
                    (requireActivity() as MainActivity).showMessage("Video original not found")
                }
            }

            share.setOnClickListener {
                requireContext().share(
                    mediaInfo.isVideo,
                    Utils.getUri(requireContext(), mediaInfo.path)
                )
            }

            delete.setOnClickListener {
                dismissNow()
                onDelete()
            }
        }
    }

    private fun startCompareActivity(item: Pair<MediaInfo, MediaInfo>) {
        val intent = Intent(context, CompareActivity::class.java)
        intent.putExtra("Media", item)
        requireContext().startActivity(intent)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setData() {
        binding.apply {
            name.text = mediaInfo.name
            size.text = Formatter.formatFileSize(context, mediaInfo.size)
            time.text = mediaInfo.time

            Glide.with(this@MoreBottomSheetFragment)
                .load(mediaInfo.path)
                .error(requireContext().getDrawable(R.drawable.logo))
                .into(binding.thumbnail);
        }
    }
}