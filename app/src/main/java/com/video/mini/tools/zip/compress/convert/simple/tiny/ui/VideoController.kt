package com.video.mini.tools.zip.compress.convert.simple.tiny.ui

import android.view.View
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class VideoController(private val videoController: PlayerView) {
    lateinit var exoPlayer: ExoPlayer
    fun setUpVideoController(path: String, isShowSetting: Boolean = true) {
        exoPlayer = ExoPlayer.Builder(videoController.context).build()
        videoController.findViewById<View>(androidx.media3.ui.R.id.exo_settings).isVisible =
            isShowSetting
        videoController.player = exoPlayer
        exoPlayer.setMediaItem(MediaItem.fromUri(path))
        exoPlayer.prepare()
    }
}