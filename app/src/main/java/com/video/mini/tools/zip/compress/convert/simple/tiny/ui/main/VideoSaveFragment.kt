package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.main

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils
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
            visibiNoti()
        }
    }

    private fun visibiNoti() {
        binding.noti.visibility = if (videoAdapter.submitData.isEmpty()) View.VISIBLE else View.GONE
        binding.rcy.visibility =
            if (videoAdapter.submitData.isNotEmpty()) View.VISIBLE else View.GONE
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

    lateinit var mContext: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext=context
    }

    private fun handleDeleteMedia(mediaInfo: MediaInfo) {
        val videoSaveResult = HandleSaveResult(mContext)
        videoSaveResult.delete(mediaInfo.name)
        if (File(mediaInfo.path).delete()) {
            val position = videoAdapter.submitData.indexOf(mediaInfo)
            videoAdapter.notifyItemRemoved(position)
            videoAdapter.submitData.removeAt(position)
        } else {
            Toast.makeText(requireContext(), "Delete unsuccessful", Toast.LENGTH_SHORT).show()
        }
        visibiNoti()
    }

}