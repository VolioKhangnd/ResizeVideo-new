package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.process

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import com.arthenica.ffmpegkit.FFmpegKit
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.DialogClickListener
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivityProcessBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.IProcessFFmpeg
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.MediaAction
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.VideoCommandProcessor
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.VideoProcess
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.OptionCompressType
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.OptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.StateCompression
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.error.ShowErrorActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.main.MainActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.result.ResultActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.DialogUtils
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.HandleMediaVideo
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.getActionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.getOptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passActionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passMediaInput
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passMediaOutput
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils.startToMainActivity
import java.util.Collections

class ProcessActivity : BaseActivity<ActivityProcessBinding>(), IProcessFFmpeg {

    private lateinit var optionMedia: OptionMedia

    override fun onBackPressed() {
        super.onBackPressed()
        showBackDialog()
    }

    private fun showBackDialog() {
        DialogUtils.showDialogBack(this, object : DialogClickListener {
            override fun onPositive() {
                startToMainActivity()
                FFmpegKit.cancel()
            }

            override fun onNegative() {

            }
        })
    }

    private lateinit var listInput: List<String>
    override fun initData() {
        optionMedia = intent.getOptionMedia()!!
    }

    override fun initView() {
        binding.apply {
            showNativeAds(nativeAds) {}
            setupViewPager()
        }
    }

    private var processAdapter: ProcessAdapter = ProcessAdapter(this)
    private fun setupViewPager() {
        processAdapter.submitData = optionMedia.dataOriginal
        binding.viewPager.adapter = processAdapter
        if (intent.getActionMedia() == MediaAction.JoinVideo) {
            processAdapter.submitData.forEach { it.stateCompression = StateCompression.Processing }
            processAdapter.notifyDataSetChanged()
        }
        compressVideo()
    }

    private fun compressVideo() {
        binding.progressBar.totalProgress = 100f
        binding.progressBar.format("%.0f%%")

        val handle = HandleMediaVideo(this)
        val videoCommandProcessor = VideoCommandProcessor(
            this,
            handle.getPathVideoCacheFolder(),
            handle.getPathAudioCacheFolder()
        )
        listInput = videoCommandProcessor.createCommandList(optionMedia)
        if (optionMedia.optionCompressType is OptionCompressType.CustomFileSize) {
            VideoProcess.Builder(this, optionMedia)
                .twoCompressAsync(listInput, this)
        } else {
            VideoProcess.Builder(this, optionMedia)
                .compressAsync(listInput, this)
        }
    }

    override fun getViewBinding(): ActivityProcessBinding {
        return ActivityProcessBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun processElement(currentElement: Int, percentage: Int) {
        runOnUiThread {
            binding.apply {
                position.text = "${currentElement + 1}/${listInput.size}"
                progressBar.currentProgress = percentage.toFloat()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            showBackDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCurrentElement(position: Int) {
        runOnUiThread {
            processAdapter.submitData[0].stateCompression = StateCompression.Processing
            processAdapter.notifyItemChanged(position)
        }
    }

    override fun onSuccess(currentIndex: Int, mediaInfo: MediaInfo) {
        Log.d("sdfaklfjaklsjfkonSuccess", mediaInfoResults.toString())
        mediaInfoResults.add(mediaInfo)

        runOnUiThread {
            val lastItemIndex = processAdapter.itemCount - 1
            processAdapter.submitData[currentIndex].stateCompression = StateCompression.Success

            processAdapter.notifyItemMoved(0, lastItemIndex)
            Collections.swap(processAdapter.submitData, 0, lastItemIndex)
        }
    }

    private val mediaInfoResults: ArrayList<MediaInfo> = ArrayList()
    override fun onFinish() {
        Log.d("sdfaklfjaklsjfkonFinish", mediaInfoResults.toString())
        if (mediaInfoResults.isEmpty()) {
            val intent = Intent(this, ShowErrorActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            return
        }
        val intent = Intent(this, ResultActivity::class.java)
        intent.passMediaOutput(mediaInfoResults)
        intent.passMediaInput(this.intent.getOptionMedia()!!.dataOriginal)
        intent.passActionMedia(this.intent.getActionMedia()!!)
        startActivity(intent)
        finish()
    }

    override fun onFailure(position: Int, error: String) {
        Log.d("sdfaklfjaklsjfkononFailure", mediaInfoResults.toString())

        runOnUiThread {
            if (intent.getActionMedia() == MediaAction.JoinVideo) {
                processAdapter.submitData.forEach { it.stateCompression = StateCompression.Failure }
                processAdapter.notifyDataSetChanged()
            }
            val lastItemIndex = processAdapter.itemCount - 1
            processAdapter.submitData[position].stateCompression = StateCompression.Failure
            processAdapter.notifyItemChanged(position)
            Collections.swap(processAdapter.submitData, position, lastItemIndex)
            processAdapter.notifyItemMoved(position, lastItemIndex)
        }
    }
}
