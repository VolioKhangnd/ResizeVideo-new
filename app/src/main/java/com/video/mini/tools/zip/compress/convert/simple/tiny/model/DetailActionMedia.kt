package com.video.mini.tools.zip.compress.convert.simple.tiny.model

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.MediaAction

data class DetailActionMedia(
    val media: MediaAction,
    val title: String,
    val icon: Drawable,
    val backGroundAction: Int,
    val backGroundIcon: Int,
    val isSubVip: Boolean = false
)


@SuppressLint("UseCompatLoadingForDrawables")
fun Context.getListDetailsActionMedia(): List<DetailActionMedia> {
    return listOf(
        DetailActionMedia(
            MediaAction.CutTrim,
            "Video Cutter",
            getDrawable(R.drawable.ic_cutter)!!,
            getColor(R.color.bg_video_cutter),
            getColor(R.color.bg_ic_video_cutter)
        ),
        DetailActionMedia(
            MediaAction.JoinVideo,
            "Join Video",
            getDrawable(R.drawable.ic_join_video)!!,
            getColor(R.color.bg_join_video),
            getColor(R.color.bg_ic_join_video)
        ),
        DetailActionMedia(
            MediaAction.FastForward,
            "Fast Forward",
            getDrawable(R.drawable.ic_fast_forward)!!,
            getColor(R.color.bg_fast_forward),
            getColor(R.color.bg_ic_fast_forward)
        ),
        DetailActionMedia(
            MediaAction.ReveresVideo,
            "Reveres Video",
            getDrawable(R.drawable.ic_reveres_video)!!,
            getColor(R.color.bg_reveres_video),
            getColor(R.color.bg_ic_reveres_video)
        ),
        DetailActionMedia(
            MediaAction.SlowVideo,
            "Slow Video",
            getDrawable(R.drawable.ic_slow_vd)!!,
            getColor(R.color.bg_slow_video),
            getColor(R.color.bg_ic_slow_video),
        ),
        DetailActionMedia(
            MediaAction.ExtractAudio,
            "Extract Audio",
            getDrawable(R.drawable.ic_extract_adi)!!,
            getColor(R.color.bg_extract_audio),
            getColor(R.color.bg_ic_extract_audio),
        ),
    )
}