package com.tearas.resizevideo.ui.cut_trim


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Looper
import android.view.MenuItem
import android.view.View
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.exoplayer.ExoPlayer
import com.arthenica.ffmpegkit.FFprobeKit
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.tearas.resizevideo.ui.main.MainActivity
import com.tearas.resizevideo.core.BaseActivity
import com.tearas.resizevideo.R
import com.tearas.resizevideo.databinding.ActivityCutTrimBinding
import com.tearas.resizevideo.ffmpeg.MediaAction
import com.tearas.resizevideo.model.OptionMedia
import com.tearas.resizevideo.ui.VideoController
import com.tearas.resizevideo.ui.select_compress.SelectCompressActivity
import com.tearas.resizevideo.utils.IntentUtils.getActionMedia
import com.tearas.resizevideo.utils.IntentUtils.getOptionMedia
import com.tearas.resizevideo.utils.IntentUtils.passActionMedia
import com.tearas.resizevideo.utils.IntentUtils.passOptionMedia
import com.tearas.resizevideo.utils.Utils
import com.tearas.resizevideo.utils.Utils.isDarkMode


class CutTrimActivity : BaseActivity<ActivityCutTrimBinding>() {
    override fun getViewBinding(): ActivityCutTrimBinding {
        return ActivityCutTrimBinding.inflate(layoutInflater)
    }

    private var left = 0f
    private var right = 0f
    private var isPlaying: Boolean = false
    private lateinit var path: String
    private lateinit var optionMedia: OptionMedia
    private lateinit var runnable: Runnable
    private var idClick = R.id.cutVideo
    private lateinit var exoPlayer: ExoPlayer

    override fun initData() {
        optionMedia = intent.getOptionMedia()!!
        val media = optionMedia.dataOriginal[0]
        path = media.path
        val videoController = VideoController(binding.video)
        videoController.setUpVideoController(path, false)
        exoPlayer = videoController.exoPlayer
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {

        binding.apply {
            setToolbar(
                binding.toolbar,
                "Edit Video",
                getDrawable(R.drawable.baseline_arrow_back_24)!!
            )

            setUpDefaultRangeSeekBar(path)
            setAdapterFrameVideo()
            exoPlayer.addListener(object : Listener {
                override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                    if (playWhenReady) {
                        handler.removeCallbacksAndMessages(null)
                        handler.post(runnable)
                    } else {
                        handler.removeCallbacksAndMessages(null)
                    }
                }
            })

            setColorIndicator(idClick)
            setUpUICutVideo()
            setColorIndicator(idClick)
            handler.removeCallbacksAndMessages(null)
            cutVideo.setOnClickListener {
                idClick = it.id
                setUpUICutVideo()
                setColorIndicator(idClick)
                handler.removeCallbacksAndMessages(null)
                handler.post(runnable)
            }
            trimVideo.setOnClickListener {
                idClick = it.id
                setUpUITrimVideo()
                setColorIndicator(idClick)
                handler.removeCallbacksAndMessages(null)
                handler.post(runnable)
            }
        }
    }

    private fun setColorIndicator(id: Int) {
        val color1: Int
        val color2: Int

        if (id != R.id.trimVideo) {
            color1 = getColor(R.color.buttonTintCb)
            color2 = getColor(R.color.bg_click)
        } else {
            color1 = getColor(R.color.bg_click)
            color2 = getColor(R.color.buttonTintCb)
        }

        binding.indicator1.setBackgroundColor(color1)
        binding.indicator2.setBackgroundColor(color2)
    }

    override fun getMenu(): Int {
        return R.menu.menu_done
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.done) {
            binding.progressBar.visibility = View.VISIBLE
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, SelectCompressActivity::class.java)
                intent.passOptionMedia(createOptionMedia())
                intent.passActionMedia(this@CutTrimActivity.intent.getActionMedia()!!)
                startActivity(intent)
                binding.progressBar.visibility = View.GONE
            }, 1000)
            return true
        }
        if (item.itemId == android.R.id.home) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createOptionMedia(): OptionMedia {
        return OptionMedia(
            dataOriginal = optionMedia.dataOriginal,
            mediaAction = if (idClick == R.id.trimVideo) MediaAction.CutOrTrim.TrimVideo else MediaAction.CutOrTrim.CutVideo,
            endTime = (right / 1000).toLong(),
            startTime = (left / 1000).toLong()
        )
    }

    val handler = android.os.Handler(Looper.getMainLooper()!!)

    init {
        val runnable1 = Runnable {
            handler.post(runnable)
        }
        runnable = Runnable {
            if (idClick == R.id.trimVideo) {
                exoPlayer.seekTo(left.toLong())
                handler.postDelayed(runnable1, (right - left).toLong())
            } else {
                val duration =
                    FFprobeKit.getMediaInformation(path).mediaInformation.duration.toFloat()
                        .toLong() * 1000
                var firstPartStartTime = 0L
                var firstPartEndTime = 0L
                var secondPartStartTime = 0L
                var secondPartEndTime = 0L

                if (left > 0) {
                    firstPartStartTime = 0L
                    firstPartEndTime = left.toLong()
                    secondPartStartTime = right.toLong()
                    secondPartEndTime = duration
                } else {
                    firstPartStartTime = right.toLong()
                    firstPartEndTime = duration
                    secondPartStartTime = duration
                    secondPartEndTime = duration
                }
                exoPlayer.seekTo(firstPartStartTime)

                handler.postDelayed({
                    exoPlayer.seekTo(secondPartStartTime)
                }, firstPartEndTime)

                handler.postDelayed({
                    handler.post(runnable)
                }, secondPartEndTime)
            }
        }
    }

    private fun setUpUICutVideo() {
        binding.includeCutTrim.apply {
            setRangeSeekBarColorTopBottom(getColor(R.color.maintream), getColorConfig())
            setThumbAndColorProgress(false)
            setProgress()
            rangeProgress.setProgress(left, right)
        }
    }

    private fun setUpUITrimVideo() {
        binding.includeCutTrim.apply {
            setRangeSeekBarColorTopBottom(getColorConfig(), getColor(R.color.maintream))
            binding.includeCutTrim.rangeProgress.progressDefaultColor = getColor(R.color.maintream1)
            setThumbAndColorProgress(true)
            setProgress()
            rangeProgress.setProgress(left, right)
        }
    }

    private fun setRangeSeekBarColorTopBottom(
        progressDefaultColor: Int, progressColor: Int
    ) {
        binding.includeCutTrim.apply {
            rangeTop.setProgressColor(progressDefaultColor, progressColor)
            rangeBottom.setProgressColor(progressDefaultColor, progressColor)
        }
    }


    private fun setThumbAndColorProgress(isTrim: Boolean) {
        binding.includeCutTrim.apply {
            rangeProgress.leftSeekBar.thumbDrawableId =
                if (isTrim) R.drawable.custom_thumb else R.drawable.custom_thumb
            rangeProgress.rightSeekBar.thumbDrawableId =
                if (!isTrim) R.drawable.custom_thumb else R.drawable.custom_thumb
            if (isTrim) {
                rangeProgress.setProgressColor(getColor(R.color.maintream1), Color.TRANSPARENT)
            } else {
                rangeProgress.setProgressColor(Color.TRANSPARENT, getColor(R.color.maintream1))
            }
        }
    }

    private fun getColorConfig() =
        if (isDarkMode()) {
            getColor(R.color.bg_dark_screen_2)
        } else {
            Color.parseColor("#FF9800")
        }

    private fun setProgress() {
        binding.includeCutTrim.apply {
            rangeBottom.setProgress(left, right)
            rangeTop.setProgress(left, right)
            rangeTime.setProgress(left, right)
        }
    }

    private fun setAdapterFrameVideo() {
        val frameRateVideoAdapter = FramRateVideoApdater(this@CutTrimActivity)
        frameRateVideoAdapter.submitData = FrameRateUtils.getFrames(path)
        binding.includeCutTrim.recyclerView.adapter = frameRateVideoAdapter
    }

    private fun setUpDefaultRangeSeekBar(path: String) {

        binding.includeCutTrim.apply {
            setUnEnabled(rangeBottom, rangeTop, rangeTime)
            right = FFprobeKit.getMediaInformation(path).mediaInformation.duration.toFloat() * 1000
            setDefaultProgress(right)
            setUpUITrimVideo()

            rangeTime.rightSeekBar.setIndicatorText(
                Utils.formatTime(right.toLong())
            )

            rangeTime.leftSeekBar.setIndicatorText(
                "00:00:00"
            )

            rangeProgress.setOnRangeChangedListener(object : OnRangeChangedListener {
                override fun onRangeChanged(
                    view: RangeSeekBar?,
                    leftValue: Float,
                    rightValue: Float,
                    isFromUser: Boolean
                ) {


                    left = leftValue
                    right = rightValue

                    if (!isFromUser) {
                        handler.removeCallbacksAndMessages(null)
                        handler.post(runnable)
                    }

                    setProgress()

                    rangeTime.leftSeekBar.setIndicatorText(
                        Utils.formatTime(leftValue.toLong())
                    )

                    rangeTime.rightSeekBar.setIndicatorText(
                        Utils.formatTime(rightValue.toLong())
                    )

                }

                override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

                }

                override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

                }
            })
        }
    }

    private fun setUnEnabled(vararg range: RangeSeekBar) {
        range.forEach {
            it.isEnabled = false
        }
    }

    private fun setDefaultProgress(timeSecond: Float) {
        binding.includeCutTrim.apply {
            val array = arrayOf(rangeTop, rangeBottom, rangeProgress, rangeTime)
            array.forEach {
                it.setRange(0f, timeSecond)
                it.setProgress(0f, timeSecond)
            }
        }
    }

}