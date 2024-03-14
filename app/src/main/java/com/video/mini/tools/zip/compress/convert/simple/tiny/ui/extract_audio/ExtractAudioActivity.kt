package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.extract_audio

import android.annotation.SuppressLint
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivityExtractAudioBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.IProcessFFmpeg
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.MediaAction
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.VideoCommandProcessor
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.VideoProcess
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.OptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.DialogLoading
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.HandleMediaVideo
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.getOptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils.startActivityResult


class ExtractAudioActivity : BaseActivity<ActivityExtractAudioBinding>(), IProcessFFmpeg {
    override fun getViewBinding(): ActivityExtractAudioBinding {
        return ActivityExtractAudioBinding.inflate(layoutInflater)
    }

    private lateinit var media: OptionMedia
    private lateinit var videoInfo: MediaInfo
    private lateinit var viewModel: ExtractViewModel
    private lateinit var dialogLoading: DialogLoading

    override fun initData() {
        viewModel = ViewModelProvider(this@ExtractAudioActivity)[ExtractViewModel::class.java]
        media = intent.getOptionMedia()!!
        videoInfo = media.dataOriginal[0]
        binding.extractViewModel = viewModel
        binding.lifecycleOwner = this
        dialogLoading = DialogLoading()
    }

    override fun initObserver() {
        val handle = HandleMediaVideo(this@ExtractAudioActivity)
        val videoCommandProcessor = VideoCommandProcessor(
            this@ExtractAudioActivity,
            handle.getPathVideoCacheFolder(),
            handle.getPathAudioCacheFolder()
        )

        val videoProcess = VideoProcess.Builder(this@ExtractAudioActivity, media)

        viewModel.onClickContinues.observe(this@ExtractAudioActivity) { typeAudio ->
            dialogLoading.show(supportFragmentManager, DialogLoading::class.simpleName)
            val listInput = videoCommandProcessor.createCommandList(
                media.copy(
                    mimetype = typeAudio,
                    mediaAction = MediaAction.ExtractAudio
                )
            )
            videoProcess.compressAsync(
                listInput,
                this@ExtractAudioActivity
            )
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        binding.mediaInfo = videoInfo
        setToolbar(
            binding.toolbar,
            "Audio Options",
            getDrawable(R.drawable.baseline_arrow_back_24)!!
        )
        showNativeAds(binding.nativeAds) {}
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onFailure(currentIndex: Int, error: String) {
        dialogLoading.dismiss()
        showMessage(error)
    }

    override fun onSuccess(currentIndex: Int, mediaInfo: MediaInfo) {
        mediaInfoOutput.add(mediaInfo)
        dialogLoading.dismiss()
    }

    private val mediaInfoOutput: ArrayList<MediaInfo> = arrayListOf()

    override fun onFinish() {
        startActivityResult(media.dataOriginal, mediaInfoOutput)
    }

    override fun processElement(currentElement: Int, percentage: Int) {
    }


}