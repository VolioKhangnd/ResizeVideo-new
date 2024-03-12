package com.tearas.resizevideo.ui.reverse

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tearas.resizevideo.R
import com.tearas.resizevideo.core.BaseActivity
import com.tearas.resizevideo.databinding.ActivityReversesBinding
import com.tearas.resizevideo.ffmpeg.MediaAction
import com.tearas.resizevideo.generated.callback.OnClickListener
import com.tearas.resizevideo.model.MediaInfos
import com.tearas.resizevideo.model.OptionMedia
import com.tearas.resizevideo.ui.VideoController
import com.tearas.resizevideo.ui.process.ProcessActivity
import com.tearas.resizevideo.utils.IntentUtils.getActionMedia
import com.tearas.resizevideo.utils.IntentUtils.getOptionMedia
import com.tearas.resizevideo.utils.IntentUtils.passActionMedia
import com.tearas.resizevideo.utils.IntentUtils.passOptionMedia

class ReversesActivity : BaseActivity<ActivityReversesBinding>() {
    override fun getViewBinding() = ActivityReversesBinding.inflate(layoutInflater)

    override fun initView() {
        showBannerAds(binding.bannerAds)
        setUpVideo()
        binding.mToolbar.setNavigationOnClickListener {
            finish()
        }
        binding.next.setOnClickListener {
            OptionsReverseFragmentBS().show(supportFragmentManager,OptionsReverseFragmentBS::class.simpleName)
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