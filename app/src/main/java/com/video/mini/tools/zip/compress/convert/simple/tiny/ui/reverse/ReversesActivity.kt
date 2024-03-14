package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.reverse

import android.content.Intent
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivityReversesBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.MediaAction
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.OptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.VideoController
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.process.ProcessActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.getOptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passActionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passOptionMedia

class ReversesActivity : BaseActivity<ActivityReversesBinding>() {
    override fun getViewBinding() = ActivityReversesBinding.inflate(layoutInflater)

    override fun initView() {
        showBannerAds(binding.bannerAds)
        setUpVideo()
        binding.mToolbar.setNavigationOnClickListener {
            finish()
        }
        binding.next.setOnClickListener {
            OptionsReverseFragmentBS().show(supportFragmentManager, OptionsReverseFragmentBS::class.simpleName)
        }
    }


    private fun setUpVideo() {
        binding.apply {
            val path = intent.getOptionMedia()!!.dataOriginal[0].path
            val videoController = VideoController(playerView)
            videoController.setUpVideoController(path, false)
        }
    }

    fun onDone(reverseAudio: Boolean, preset: String) {
        val intent = Intent(this, ProcessActivity::class.java)
        intent.passOptionMedia(createOptionMedia(reverseAudio, preset))
        intent.passActionMedia(MediaAction.ReveresVideo)
        startActivity(intent)
    }

    private fun createOptionMedia(reverseAudio: Boolean, preset: String): OptionMedia {
        return intent.getOptionMedia()!!.copy(
            mediaAction = MediaAction.ReveresVideo,
            reverseAudio = reverseAudio,
            preset = preset,
        )
    }
}