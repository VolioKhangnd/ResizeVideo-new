package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.video_pickers

import android.icu.text.Transliterator.Position
import android.provider.MediaStore
import android.view.View
import android.view.animation.AnimationUtils
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
        val mediaAction = requireActivity().intent.getActionMedia()!!
        val isSubVip = (requireActivity() as MainPickerActivity).proApplication.isSubVip
        adapter = VideoAdapter(requireActivity(),
            viewModel.videos,
            mediaAction,
            isSubVip,
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
                getItemPosition(mediaInfo) { position ->
                    adapter.submitData[position].isSelected = false
                    adapter.notifyItemChanged(position)
                    binding.videoAdapter.findViewHolderForAdapterPosition(position)?.itemView?.startAnimation(
                        AnimationUtils.loadAnimation(context, R.anim.scale_up)
                    )
                }
            }
            viewModel.videos.clear()
        }

        viewModel.videosLiveData.observe(this) { info ->
            getItemPosition(info) { position ->
                adapter.notifyItemChanged(
                    position,
                    adapter.submitData[position].apply { isSelected = info.isSelected })
            }
        }
    }

    private fun getItemPosition(mediaInfo: MediaInfo, position: (Int) -> Unit) {
        val index = adapter.submitData.indexOfLast { it.id == mediaInfo.id }
        index.takeIf { it != -1 }?.let { idx ->
            position(idx)
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