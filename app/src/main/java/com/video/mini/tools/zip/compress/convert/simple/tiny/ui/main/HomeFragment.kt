package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.main

import android.content.Context
import android.content.Intent
import android.view.View
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseFragment
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.FragmentHomeBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.MediaAction
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.DetailActionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.getListDetailsActionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.sup_vip.SubVipActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.AnimUtils
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.VibrateUtils

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    override fun getViewBinding(view: View) = FragmentHomeBinding.bind(view)
    private lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun initView() {
        binding.apply {
            compressVideo.setOnClickListener {
                VibrateUtils.vibrate(it.context, 50)
                AnimUtils.scaleClickView(it) {
                    mainActivity.startPickerVideo(MediaAction.CompressVideo)
                }
            }
            mainActivity.showNativeAds(container, {})
        }
        setUpAdapter()
    }

    private fun setUpAdapter() {
        val actionAdapter = ActionVideoAdapter(object : OnClickItemRcy {
            override fun onClick(data: DetailActionMedia) {
                if (data.isSubVip) {
                    startActivity(Intent(requireContext(), SubVipActivity::class.java))
                }
                mainActivity.startPickerVideo(data.media)
            }
        })
        actionAdapter.submitData = ArrayList(requireContext().getListDetailsActionMedia())
        binding.rcyAction.adapter = actionAdapter
    }

}