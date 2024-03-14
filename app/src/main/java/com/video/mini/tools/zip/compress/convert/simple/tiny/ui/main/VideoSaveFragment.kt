package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.main

import android.view.View
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseFragment
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.DialogClickListener
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.FragmentFilesBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.compressed.MoreBottomSheetFragment
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.compressed.VideoCompressedAdapter
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.DialogUtils
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.HandleMediaVideo
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.HandleSaveResult
import java.io.File

class VideoSaveFragment : BaseFragment<FragmentFilesBinding>(R.layout.fragment_files) {
    override fun getViewBinding(view: View): FragmentFilesBinding {
        return FragmentFilesBinding.bind(view)
    }

    private lateinit var videoAdapter: VideoCompressedAdapter

    override fun initView() {
        binding.apply {

            val videoHandler = HandleMediaVideo(requireContext())

            videoAdapter = VideoCompressedAdapter(requireActivity()) {
                showMoreBottomSheet(it)
            }
             videoAdapter.submitData = videoHandler.getVideoSave()
            rcy.adapter = videoAdapter
        }
    }

    private fun showMoreBottomSheet(it: MediaInfo) {
        MoreBottomSheetFragment(it) {
            DialogUtils.showDialogDelete(requireActivity(), object : DialogClickListener {
                override fun onPositive() {
                    handleDeleteMedia(it)
                }

                override fun onNegative() {

                }
            })
        }.show(requireActivity().supportFragmentManager, MoreBottomSheetFragment::class.simpleName)
    }

    private fun handleDeleteMedia(mediaInfo: MediaInfo) {
        val videoSaveResult = HandleSaveResult(requireContext())
        videoSaveResult.delete(mediaInfo.name)
        File(mediaInfo.path).delete()
        val position = videoAdapter.submitData.indexOf(mediaInfo)
        videoAdapter.submitData.remove(mediaInfo)
        videoAdapter.notifyItemRemoved(position)
    }

}