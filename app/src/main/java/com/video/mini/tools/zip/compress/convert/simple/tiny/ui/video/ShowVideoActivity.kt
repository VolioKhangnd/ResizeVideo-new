package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.video

import android.util.Log
import android.view.MenuItem
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivityShowVideoBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.VideoController

class ShowVideoActivity : BaseActivity<ActivityShowVideoBinding>() {
    override fun getViewBinding(): ActivityShowVideoBinding {
        return ActivityShowVideoBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.apply {
            setToolbar(
                binding.toolbar,
                getString(R.string.player),
                getDrawable(R.drawable.baseline_arrow_back_24)!!
            )
            showVideo()
            showBannerAds(bannerAds)
        }
    }

    private fun showVideo() {
        val path = intent.getStringExtra("path")!!
         val videoController = VideoController(binding.video)
        videoController.setUpVideoController(path)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}