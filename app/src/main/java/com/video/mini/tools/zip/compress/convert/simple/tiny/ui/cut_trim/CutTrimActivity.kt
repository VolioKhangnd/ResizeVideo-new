package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.cut_trim


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player.Listener
import androidx.media3.exoplayer.ExoPlayer
import com.arthenica.ffmpegkit.FFprobeKit
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.theartofdev.edmodo.cropper.CropImageView
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivityCutTrimBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.MediaAction
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.OptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.Resolution
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.VideoController
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.main.MainActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.process.ProcessActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.select_compress.SelectCompressActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.getActionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.getOptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passActionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passOptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils.isDarkMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Field
import java.lang.reflect.Method


class CutTrimActivity : BaseActivity<ActivityCutTrimBinding>(), Listener {
    override fun getViewBinding(): ActivityCutTrimBinding {
        return ActivityCutTrimBinding.inflate(layoutInflater)
    }

    private var left = 0f
    private var right = 0f
    private lateinit var path: String
    private lateinit var optionMedia: OptionMedia
    private lateinit var runnable: Runnable
    private var idClick = R.id.cutVideo
    private lateinit var exoPlayer: ExoPlayer
    private val isCrop get() = binding.imgCrop.cropRect != null

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
            imgCrop.isAutoZoomEnabled = false
            setUpDefaultRangeSeekBar(path)

            exoPlayer.addListener(this@CutTrimActivity)
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

            imgCrop.guidelines = CropImageView.Guidelines.ON
            setAdapterFrameVideo()
        }
    }


    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        if (playWhenReady) {
            handler.removeCallbacksAndMessages(null)
            handler.post(runnable)
        } else {
            handler.removeCallbacksAndMessages(null)
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        showMessage("Error when playing video")
        finish()
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
        when (item.itemId) {
            R.id.done -> clickMenuDone()
            R.id.crop -> clickMenuCrop(item)
            R.id.home -> clickMenuHome()

        }
        return true
    }

    private fun clickMenuHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun clickMenuCrop(item: MenuItem) {
        val popupMenu = PopupMenu(this, findViewById(R.id.crop))
        binding.imgCrop.setFixedAspectRatio(true)
        popupMenu.setOnMenuItemClickListener {
            binding.imgCrop.isShowCropOverlay = true
            when (it.itemId) {
                R.id.custom -> {
                    val bitmap = Bitmap.createBitmap(
                        exoPlayer.videoSize.width,
                        exoPlayer.videoSize.height,
                        Config.ARGB_8888
                    )
                    binding.imgCrop.setImageBitmap(bitmap)
                    binding.imgCrop.resetCropRect()
                 }

                R.id.square -> binding.imgCrop.setAspectRatio(1, 1)
                R.id.portrait -> binding.imgCrop.setAspectRatio(1, 2)
                R.id.landscape -> binding.imgCrop.setAspectRatio(2, 1)
                R.id.c4_3 -> binding.imgCrop.setAspectRatio(4, 3)
                R.id.c16_9 -> binding.imgCrop.setAspectRatio(16, 9)
                R.id.cN -> {
                    binding.imgCrop.resetCropRect()
                    binding.imgCrop.isShowCropOverlay = false
                }
            }
            true
        }
        popupMenu.inflate(R.menu.menu_crop)

        popupMenu.show()

    }

    private fun clickMenuDone() {
        binding.progressBar.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            val destination =
                if (isCrop) ProcessActivity::class.java else SelectCompressActivity::class.java
            val intent = Intent(this, destination)
            intent.passOptionMedia(createOptionMedia())
            intent.passActionMedia(this.intent.getActionMedia()!!)
            startActivity(intent)
            binding.progressBar.visibility = View.GONE
        }, 1000)
    }


    private fun createOptionMedia(): OptionMedia {
        val resolutionOrigin = optionMedia.dataOriginal[0]
        val resolutionResize = exoPlayer.videoSize

        return if (isCrop) {
            val ratioWidth = resolutionOrigin.resolution!!.width.toFloat() / resolutionResize.width
            val ratioHeight =
                resolutionOrigin.resolution!!.height.toFloat() / resolutionResize.height

            val cropRectWidth = (binding.imgCrop.cropRect.right - binding.imgCrop.cropRect.left)
            val cropRectHeight = (binding.imgCrop.cropRect.bottom - binding.imgCrop.cropRect.top)

            val outWidth = (cropRectWidth * ratioWidth).toInt()
            val outHeight = (cropRectHeight * ratioHeight).toInt()

            val pointXResize = binding.imgCrop.cropRect.left
            val pointYResize = binding.imgCrop.cropRect.top

            val pointXOrigin = pointXResize * ratioWidth
            val pointYOrigin = pointYResize * ratioHeight

            this.optionMedia.copy(
                dataOriginal = optionMedia.dataOriginal,
                mediaAction = if (idClick == R.id.trimVideo) MediaAction.CutTrimCrop.TrimVideo(true) else MediaAction.CutTrimCrop.CutVideo(
                    true
                ),
                endTime = (right / 1000).toLong(),
                startTime = (left / 1000).toLong(),
                x = pointXOrigin.toInt(),
                y = pointYOrigin.toInt(),
                newResolution = Resolution(outWidth, outHeight)
            )
        } else {
            this.optionMedia.copy(
                dataOriginal = optionMedia.dataOriginal,
                mediaAction = if (idClick == R.id.trimVideo) MediaAction.CutTrimCrop.TrimVideo() else MediaAction.CutTrimCrop.CutVideo(),
                endTime = (right / 1000).toLong(),
                startTime = (left / 1000).toLong()
            )
        }
    }


    val handler = Handler(Looper.getMainLooper()!!)

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
        val frameRateVideoAdapter = FrameRateVideoAdapter(this@CutTrimActivity)
        binding.includeCutTrim.recyclerView.adapter = frameRateVideoAdapter
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                val frames = FrameRateUtils.getFrames(path)
                frameRateVideoAdapter.submitData = frames
            }
        }
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
                    setProgress()

                    rangeTime.leftSeekBar.setIndicatorText(
                        Utils.formatTime(leftValue.toLong())
                    )

                    rangeTime.rightSeekBar.setIndicatorText(
                        Utils.formatTime(rightValue.toLong())
                    )

                }

                override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
                    Handler(Looper.getMainLooper()).post {
                        handler.removeCallbacksAndMessages(null)
                        handler.post(runnable)
                    }
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