package com.video.mini.tools.zip.compress.convert.simple.tiny.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import com.video.mini.tools.zip.compress.convert.simple.tiny.R

object AnimUtils {
    fun zoomInOutView(view: View) {
        val animUtils = android.view.animation.AnimationUtils.loadAnimation(
            view.context,
            R.anim.anim_zoom_in_out
        )
        view.startAnimation(animUtils)
    }

    fun scaleClickView(view: View, endAnim: (() -> Unit)? =null) {
        val animUtils = android.view.animation.AnimationUtils.loadAnimation(
            view.context,
            R.anim.scale_btn_click
        )
        animUtils.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                if (endAnim != null) {
                    endAnim()
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

        })
        view.startAnimation(animUtils)
    }
    fun dropDownView(view:View){
        val animUtils = android.view.animation.AnimationUtils.loadAnimation(
            view.context,
            R.anim.drop_down
        )
        view.startAnimation(animUtils)
    }
    fun pullUpView(view:View,endAnim: (() -> Unit)? =null){
        val animUtils = android.view.animation.AnimationUtils.loadAnimation(
            view.context,
            R.anim.pull_up
        )
        animUtils.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                if (endAnim != null) {
                    endAnim()
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

        })
        view.startAnimation(animUtils)
    }
}