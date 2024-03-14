package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.extract_audio

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.main.MainActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.AcitivityShowAudioBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.HandleMediaVideo
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils.startToMainActivity

class ShowAudioActivity : BaseActivity<AcitivityShowAudioBinding>() {
    override fun getViewBinding(): AcitivityShowAudioBinding {
        return AcitivityShowAudioBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.apply {
            showBannerAds(bannerAds)
            setToolbar(
                binding.toolbar,
                getString(R.string.extracted_audios_title),
                getDrawable(R.drawable.baseline_arrow_back_24)!!
            )
            val handleMediaVideo = HandleMediaVideo(this@ShowAudioActivity)

            val adapter = ShowAudioCompressedAdapter(this@ShowAudioActivity)
            rcyAudio.adapter = adapter

            Handler(Looper.getMainLooper()).post {
                adapter.submitData = handleMediaVideo.getAudioSave()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startToMainActivity()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}