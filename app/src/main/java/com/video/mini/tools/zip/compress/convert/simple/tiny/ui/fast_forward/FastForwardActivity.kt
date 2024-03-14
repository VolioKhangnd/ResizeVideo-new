package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.fast_forward

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.view.MenuItem
import android.widget.TextView
import androidx.media3.exoplayer.ExoPlayer
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivityFastForwardBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.MediaAction
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.OptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.VideoController
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.getActionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.getOptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passOptionMedia


class FastForwardActivity : BaseActivity<ActivityFastForwardBinding>() {
    override fun getViewBinding(): ActivityFastForwardBinding {
        return ActivityFastForwardBinding.inflate(layoutInflater)
    }

    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying: Boolean = false
    private var speed: Float = 1.0F
    private lateinit var options: Array<TextView>
    private var speeds: HashMap<Int, Float> = hashMapOf(
        R.id.txt1x to 1.25f,
        R.id.txt1_25x to 1.5f,
        R.id.txt1_5x to 2f,
        R.id.txt2x to 2.5f,
        R.id.txt2_5x to 3f,
    )

    override fun initData() {


    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n", "CheckResult")
    override fun initView() {
        options = arrayOf(
            binding.txt1x,
            binding.txt125x,
            binding.txt15x,
            binding.txt2x,
            binding.txt25x,
            binding.customSpeed
        )
        if (intent.getActionMedia() is MediaAction.SlowVideo) {
            speeds = hashMapOf(
                R.id.txt1x to 0.2f,
                R.id.txt1_25x to 0.25f,
                R.id.txt1_5x to 0.5f,
                R.id.txt2x to 0.7f,
                R.id.txt2_5x to 0.75f,
            )
        }
        options.forEachIndexed { index, textView ->
            if (textView.id != binding.customSpeed.id) textView.text =
                speeds[textView.id].toString() + "x"
        }
        setBackground(binding.txt1x.id)
        binding.apply {
            val actionMedia = intent.getActionMedia()!!
            setToolbar(
                binding.toolbar,
                if (actionMedia is MediaAction.FastForward) "Fast Forward" else "Slow Video",
                getDrawable(R.drawable.baseline_arrow_back_24)!!
            )
            setUpVideo()
            setOnClickSpeed()
            setUpVideo()
        }
    }

    private fun setOnClickSpeed() {
        options.forEach { it ->
            it.setOnClickListener { it ->
                if (it.id == R.id.customSpeed) {
                    showSpeedDialog()
                } else {
                    setBackground(it.id)
                    val speed = speeds[it.id]!!
                    exoPlayer.setPlaybackSpeed(speed)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showSpeedDialog() {
        CustomSpeedDialogFragment(intent.getActionMedia() is MediaAction.FastForward) {
            exoPlayer.setPlaybackSpeed(it)
            binding.customSpeed.text = "$it x"
            setBackground(binding.customSpeed.id)
        }.show(
            supportFragmentManager, CustomSpeedDialogFragment::class.simpleName
        )
    }


    private lateinit var exoPlayer: ExoPlayer

    @SuppressLint("CheckResult")
    private fun setUpVideo() {
        binding.apply {
            val path = intent.getOptionMedia()!!.dataOriginal[0].path
            val videoController = VideoController(videoController)
            videoController.setUpVideoController(path, false)
            exoPlayer = videoController.exoPlayer
        }
    }

    override fun getMenu(): Int {
        return R.menu.menu_done
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.done) {
            val intent = Intent(this, FastForwardOptionsActivity::class.java)
            intent.passOptionMedia(createOptionMedia())
            startActivity(intent)
            return true
        }
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setBackground(idChecked: Int) {
        options.forEach {
            it.setTextColor(getColor(R.color.buttonTintCb))
            it.background =
                if (it.id == idChecked) {
                    getDrawable(R.drawable.radio_speed_checked)
                } else {
                    getDrawable(R.drawable.radio_speed_unchecked)
                }
        }
    }


    private fun createOptionMedia(): OptionMedia {
        return intent.getOptionMedia()!!.copy(
            speed = exoPlayer.playbackParameters.speed,
            isFastVideo = intent.getActionMedia() is MediaAction.FastForward
        )
    }
}