package com.video.mini.tools.zip.compress.convert.simple.tiny.model

import androidx.annotation.DrawableRes

data class IntroModel(
    val title: String,
    val description: String,
    @DrawableRes val image: Int,
) {
}