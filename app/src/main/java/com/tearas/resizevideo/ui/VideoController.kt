package com.tearas.resizevideo.ui

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.tearas.resizevideo.R
import com.tearas.resizevideo.databinding.OverlayVideoControllerBinding
import com.tearas.resizevideo.utils.Utils

interface OnControllerVideo {
    fun onPause()
    fun onStart()

}

class VideoController : ConstraintLayout {
    private var onControllerVideo: OnControllerVideo? = null
    private var mOverlayBinding: OverlayVideoControllerBinding =
        OverlayVideoControllerBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : super(context) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initialize(context)
    }

    private var progressCurrent = 0
    private var progressTotal = 0
    private var isShowing = false
    private var isPlaying = false
    private var isOpenSetting = false
    private var mediaPlayer: MediaPlayer? = null
    private var speedBefore: TextView? = findViewById(R.id.sp1)

    init {
        mOverlayBinding.apply {
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    mOverlayBinding.timeCurrent.setTime(progress)
                    mOverlayBinding.videoView.seekTo(progress)
                    progressCurrent = progress
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
            val handleOnClick = View.OnClickListener {
                if (isOpenSetting) {
                    mSpeed.loadAnim(R.anim.fade_in)
                    isOpenSetting = false
                    return@OnClickListener
                }
                when (it) {
                    setting -> {
                        if (!isOpenSetting) {
                            mSpeed.visibility = View.VISIBLE
                            mSpeed.loadAnim(R.anim.slide_up)
                        }
                        isOpenSetting = true
                    }

                    prev -> {
                        mOverlayBinding.timeCurrent.setTime(0)
                        mOverlayBinding.videoView.seekTo(0)
                    }

                    backtrack5 -> {
                        progressCurrent -= 5000
                        mOverlayBinding.timeCurrent.setTime(progressCurrent)
                        mOverlayBinding.videoView.seekTo(progressCurrent)
                    }

                    quick15 -> {
                        progressCurrent += 15000
                        mOverlayBinding.timeCurrent.setTime(progressCurrent)
                        mOverlayBinding.videoView.seekTo(progressCurrent)

                    }

                    play -> {
                        if (isPlaying) {
                            onControllerVideo?.onPause()
                            videoView.pause()
                        } else {
                            onControllerVideo?.onStart()
                            videoView.start()
                        }
                        isPlaying = !isPlaying
                    }

                    mOverlayBinding.root -> {
                        if (isShowing) {
                            overlay.loadAnim(R.anim.fade_in)
                            mediaInfo.loadAnim(R.anim.slide_in)
                        } else {
                            overlay.visibility = View.VISIBLE
                            mediaInfo.visibility = View.VISIBLE
                            overlay.loadAnim(R.anim.fade_up)
                            mediaInfo.loadAnim(R.anim.slide_up)
                        }
                        isShowing = !isShowing
                    }
                }
            }
            prev.setOnClickListener(handleOnClick)
            backtrack5.setOnClickListener(handleOnClick)
            quick15.setOnClickListener(handleOnClick)
            play.setOnClickListener(handleOnClick)
            mOverlayBinding.root.setOnClickListener(handleOnClick)
            setting.setOnClickListener(handleOnClick)

            val handleOnClickSpeed = OnClickListener {
                val drawable = ContextCompat.getDrawable(context, R.drawable.baseline_done_24)
                speedBefore?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                (it as TextView).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

                val speed = when (mSpeed.indexOfChild(it)) {
                    0 -> 0.25f
                    1 -> 0.5f
                    2 -> 1f
                    3 -> 1.25f
                    4 -> 1.5f
                    else -> 0f
                }
                val params = PlaybackParams()
                params.speed = speed
                mediaPlayer!!.playbackParams = params
                speedBefore = it
            }
            sp025.setOnClickListener(handleOnClickSpeed)
            sp05.setOnClickListener(handleOnClickSpeed)
            sp1.setOnClickListener(handleOnClickSpeed)
            sp125.setOnClickListener(handleOnClickSpeed)
            sp15.setOnClickListener(handleOnClickSpeed)
        }
    }

    private fun View.loadAnim(anim: Int) {
        val anim = AnimationUtils.loadAnimation(this.context, anim)
        this.startAnimation(anim)
    }

    fun setPathVideo(path: String) {
        mOverlayBinding.videoView.setVideoPath(path)
        mOverlayBinding.videoView.setOnPreparedListener {
            progressTotal = it.duration
            mOverlayBinding.seekBar.max = it.duration
            mOverlayBinding.totalTime.setTime(it.duration)
            mediaPlayer = it
        }
    }

    fun setOnVideoListener(onControllerVideo: OnControllerVideo) {
        this.onControllerVideo = onControllerVideo
    }

    fun setCurrentProgress(progress: Int) {
        mOverlayBinding.timeCurrent.setTime(progress)
        mOverlayBinding.seekBar.setProgress(progress, true)
    }


    private fun TextView.setTime(timeMili: Int) {
        val time = if (timeMili < 0) 0 else timeMili
        if (time in 0..progressTotal) {
            text = Utils.formatTime(time.toLong())
        }
    }

    private fun initialize(context: Context) {
        removeAllViews()
        addView(mOverlayBinding.root)
    }
}
