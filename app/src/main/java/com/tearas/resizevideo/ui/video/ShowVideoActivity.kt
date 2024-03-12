package com.tearas.resizevideo.ui.video

import android.util.Log
import android.view.MenuItem
import com.tearas.resizevideo.R
import com.tearas.resizevideo.core.BaseActivity
import com.tearas.resizevideo.databinding.ActivityShowVideoBinding
import com.tearas.resizevideo.ui.VideoController

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
        }
    }

    private fun showVideo() {
        val path = intent.getStringExtra("path")!!
        Log.d("Âffsdfdffs", path)
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