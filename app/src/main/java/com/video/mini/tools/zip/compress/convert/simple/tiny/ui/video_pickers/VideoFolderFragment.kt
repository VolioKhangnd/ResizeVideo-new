package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.video_pickers

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs


import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseFragment
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.FragmentVideoFolderBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.FolderInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.HandleMediaVideo
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.getActionMedia

class VideoFolderFragment :
    BaseFragment<FragmentVideoFolderBinding>(R.layout.fragment_video_folder) {
    override fun getViewBinding(view: View): FragmentVideoFolderBinding {
        return FragmentVideoFolderBinding.bind(view)
    }

    private lateinit var folderInfo: FolderInfo
    private lateinit var videos: List<MediaInfo>
    private val args: VideoFolderFragmentArgs by navArgs()
    private var size = 0
    private lateinit var handlerVideo: HandleMediaVideo
    private lateinit var adapter: VideoAdapter
    private val viewModel: PickerViewModel by lazy {
        ViewModelProvider(requireActivity())[PickerViewModel::class.java]
    }

    override fun initData() {
        folderInfo = args.FolderInfo
        videos = viewModel.videos
        handlerVideo = HandleMediaVideo(requireActivity())
        adapter = VideoAdapter(requireActivity(),
            requireActivity().intent.getActionMedia()!!,
            (requireActivity() as MainPickerActivity).proApplication.isSubVip,
            object : IOnItemClickListener {
                override fun onItemClick(mediaInfo: MediaInfo) {
                    viewModel.insertVideo(mediaInfo)
                }

                override fun showNotification(isPremium: Boolean, message: String) {

                }
            })
    }



    override fun initView() {
        binding.apply {

            val iconNavigation = AppCompatResources.getDrawable(
                requireActivity(),
                R.drawable.baseline_arrow_back_24
            )!!

            setToolbar(
                mToolbar,
                folderInfo.name,
                iconNavigation,
                true
            )

            videoAdapter.adapter = adapter


            binding.mToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun initObserver() {
        viewModel.actionSort.observe(this) {
           if (isVisible) submitData(it)
        }
    }
    private fun submitData(orderBy: String) {
        val newList = handlerVideo.getVideoByIdBucket(folderInfo.id, orderBy)
        adapter.submitData = ArrayList(newList.mapIndexed { index, videoInfo ->
            val isSelected = videos.find { it.id == videoInfo.id } != null
            videoInfo.copy(isSelected = isSelected)
        })

        binding.count.text = adapter.submitData.size.toString()
        size = adapter.submitData.size
    }

    override fun onResume() {
        super.onResume()
        if (handlerVideo.compareQuantity(size)) {
            submitData(viewModel.actionSort.value.toString())
        }
    }
}