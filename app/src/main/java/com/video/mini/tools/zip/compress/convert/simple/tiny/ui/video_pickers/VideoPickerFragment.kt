package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.video_pickers

import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseFragment
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.FragmentVideoPickerBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.HandleMediaVideo
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.getActionMedia

class VideoPickerFragment :
    BaseFragment<FragmentVideoPickerBinding>(R.layout.fragment_video_picker) {

    override fun getViewBinding(view: View): FragmentVideoPickerBinding {
        return FragmentVideoPickerBinding.bind(view)
    }

    private lateinit var adapter: VideoAdapter
    private var size = 0
    private lateinit var handlerVideo: HandleMediaVideo
    private var orderBy = MediaStore.Video.Media.DATE_ADDED + " DESC"
    private val viewModel: PickerViewModel by lazy {
        ViewModelProvider(requireActivity())[PickerViewModel::class.java]
    }

    override fun initData() {
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

    override fun initObserver() {

        viewModel.actionSort.observe(this) {
            submitData(it)
        }

        viewModel.closeLiveData.observe(this) { shouldClear ->
            viewModel.videos.forEach { mediaInfo ->
                val index = adapter.submitData.indexOfLast { it.id == mediaInfo.id }
                index.takeIf { it != -1 }?.let { idx ->
                    adapter.notifyItemChanged(
                        idx,
                        adapter.submitData[index].apply { isSelected = false })
                }
            }
            viewModel.videos.clear()
        }

        viewModel.videosLiveData.observe(this) { info ->
            val index = adapter.submitData.indexOfLast { it.id == info.id }
            index.takeIf { it != -1 }?.let { idx ->
                adapter.notifyItemChanged(
                    idx,
                    adapter.submitData[index].apply { isSelected = info.isSelected })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (handlerVideo.compareQuantity(size)) {
            submitData(orderBy)
        }
    }

    override fun initView() {
        binding.apply {
            videoAdapter.adapter = adapter
        }
    }

    private fun submitData(orderBy: String) {
        adapter.submitData = handlerVideo.getAllVideo(orderBy)
        size = adapter.submitData.size
    }
}