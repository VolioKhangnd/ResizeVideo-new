package com.tearas.resizevideo.ui.process

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import com.arthenica.ffmpegkit.FFmpegKit
import com.tearas.resizevideo.core.BaseActivity
import com.tearas.resizevideo.core.DialogClickListener
import com.tearas.resizevideo.databinding.ActivityProcessBinding
import com.tearas.resizevideo.ffmpeg.IProcessFFmpeg
import com.tearas.resizevideo.ffmpeg.VideoCommandProcessor
import com.tearas.resizevideo.ffmpeg.VideoProcess
import com.tearas.resizevideo.model.MediaInfo
import com.tearas.resizevideo.model.OptionCompressType
import com.tearas.resizevideo.model.OptionMedia
import com.tearas.resizevideo.model.StateCompression
import com.tearas.resizevideo.ui.result.ResultActivity
import com.tearas.resizevideo.utils.DialogUtils
import com.tearas.resizevideo.utils.HandleMediaVideo
import com.tearas.resizevideo.utils.IntentUtils.getActionMedia
import com.tearas.resizevideo.utils.IntentUtils.getOptionMedia
import com.tearas.resizevideo.utils.IntentUtils.passActionMedia
import com.tearas.resizevideo.utils.IntentUtils.passMediaInput
import com.tearas.resizevideo.utils.IntentUtils.passMediaOutput
import com.tearas.resizevideo.utils.Utils.startToMainActivity
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
        compressVideo()
    }

    private fun compressVideo() {
        val handle = HandleMediaVideo(this)
        val videoCommandProcessor = VideoCommandProcessor(
            this,
            handle.getPathVideoCacheFolder(),
            handle.getPathVideoCacheFolder()
        )
        listInput = videoCommandProcessor.createCommandList(optionMedia)
        binding.progressBar.totalProgress = 100f
        binding.progressBar.format("%.0f%%")
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
            processAdapter.submitData[position].stateCompression = StateCompression.Processing
            processAdapter.notifyItemChanged(position)
        }
    }

    override fun onSuccess(currentIndex: Int, mediaInfo: MediaInfo) {
        runOnUiThread {
            processAdapter.submitData[currentIndex].stateCompression = StateCompression.Success
            mediaInfoResults.add(mediaInfo)
            processAdapter.notifyItemMoved(currentIndex, processAdapter.itemCount - 1)
            Collections.swap(
                processAdapter.submitData,
                currentIndex,
                processAdapter.itemCount - 1
            )
        }
    }

    private val mediaInfoResults: ArrayList<MediaInfo> = ArrayList()
    override fun onFinish() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.passMediaOutput(mediaInfoResults)
        intent.passMediaInput(this.intent.getOptionMedia()!!.dataOriginal)
        intent.passActionMedia(this.intent.getActionMedia()!!)
        startActivity(intent)
        finish()
    }

    override fun onFailure(position: Int, error: String) {
        runOnUiThread {
            processAdapter.submitData[position].stateCompression = StateCompression.Failure
            processAdapter.notifyItemChanged(position)
            if (position + 1 < processAdapter.itemCount) {
                Collections.swap(processAdapter.submitData, position, processAdapter.itemCount - 1)
                processAdapter.notifyItemMoved(position, processAdapter.itemCount - 1)
            }
        }
    }
}
