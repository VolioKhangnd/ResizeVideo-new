package com.video.mini.tools.zip.compress.convert.simple.tiny.utils

import android.view.View
import com.video.mini.tools.zip.compress.convert.simple.tiny.R

object AnimUtils {
    fun zoomInOutView(view: View) {
        val animUtils = android.view.animation.AnimationUtils.loadAnimation(
            view.context,
            R.anim.anim_zoom_in_out
        )
        view.startAnimation(animUtils)
    }
}