package com.video.mini.tools.zip.compress.convert.simple.tiny.model

import android.graphics.drawable.AdaptiveIconDrawable
import com.video.mini.tools.zip.compress.convert.simple.tiny.R

class CropModel(
      val title: String,
      val drawable: Int
) {
    companion object {
        fun getCrops() = listOf(
            CropModel("Custom", R.drawable.ic_c_custom),
            CropModel("Square", R.drawable.square),
            CropModel("Portrait", R.drawable.ic_portrait),
            CropModel("Landscape", R.drawable.ic_portrait),
            CropModel("4:3", R.drawable.ic_portrait),
            CropModel("16:9", R.drawable.ic_portrait),
            CropModel("No Crop", R.drawable.ic_c_custom),
        )
    }
}