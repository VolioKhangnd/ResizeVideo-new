package com.tearas.resizevideo.ui.main

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.tearas.resizevideo.R
import com.tearas.resizevideo.core.BaseFragment
import com.tearas.resizevideo.core.DialogClickListener
import com.tearas.resizevideo.databinding.FragmentFilesBinding
import com.tearas.resizevideo.model.MediaInfo
import com.tearas.resizevideo.ui.compressed.MoreBottomSheetFragment
import com.tearas.resizevideo.ui.compressed.VideoCompressedAdapter
import com.tearas.resizevideo.utils.DialogUtils
import com.tearas.resizevideo.utils.HandleMediaVideo
import com.tearas.resizevideo.utils.HandleSaveResult
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