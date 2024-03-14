package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.compare

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.text.format.Formatter

import android.view.MenuItem
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivityCompareBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.video.ShowVideoActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils.loadImage

class CompareActivity : BaseActivity<ActivityCompareBinding>() {
    override fun getViewBinding(): ActivityCompareBinding {
        return ActivityCompareBinding.inflate(layoutInflater)
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        setToolbar(
            binding.toolbar,
            getString(R.string.compare),
            getDrawable(R.drawable.baseline_arrow_back_24)!!
        )
        showBannerAds(binding.nativeAds)
        val media: Pair<MediaInfo, MediaInfo> =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("Media", Pair::class.java) as Pair<MediaInfo, MediaInfo>
            } else {
                intent.getSerializableExtra("Media") as Pair<MediaInfo, MediaInfo>
            }

        val mediaBefore = media.first
        val mediaAfter = media.second

        binding.apply {
            loadImage("file:///" + mediaBefore.path, binding.thumbnailBefore)
            loadImage("file:///" + mediaAfter.path, binding.thumbnailAfter)
            sizeAfter.text = Formatter.formatFileSize(this@CompareActivity, mediaAfter.size)
            sizeBefore.text = Formatter.formatFileSize(this@CompareActivity, mediaBefore.size)
            resolutionAfter.text = mediaAfter.resolution.toString()
            resolutionBefore.text = mediaBefore.resolution.toString()

            thumbnailBefore.setOnClickListener {
                startShowVideo(mediaBefore.path)
            }

            thumbnailAfter.setOnClickListener {
                startShowVideo(mediaAfter.path)
            }

        }
    }

    private fun startShowVideo(path: String) {
        val intent = Intent(this@CompareActivity, ShowVideoActivity::class.java)
        intent.putExtra("path", path)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}