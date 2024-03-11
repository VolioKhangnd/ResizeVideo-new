package com.tearas.resizevideo.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.tearas.resizevideo.ui.main.MainActivity
import com.tearas.resizevideo.R
import com.tearas.resizevideo.ffmpeg.MediaAction
import com.tearas.resizevideo.model.MediaInfo
import com.tearas.resizevideo.model.MediaInfos
import com.tearas.resizevideo.ui.result.ResultActivity
import com.tearas.resizevideo.utils.IntentUtils.passActionMedia
import com.tearas.resizevideo.utils.IntentUtils.passMediaInput
import com.tearas.resizevideo.utils.IntentUtils.passMediaOutput
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.TimeZone

object Utils {
    val formatVideos = hashMapOf(
        "mp4" to listOf(
            "h264baseline",
            "h264high",
            "h264main",
            "mpeg4",
            "mpeg2video",
            "mpeg1video",
            "libkvazaar",
            
        ),
        "gp3" to listOf("h264baseline", "h264high", "h264main", "mpeg4"),
        "asf" to listOf("h264baseline", "h264high", "h264main", "mpeg4", ),
        "avi" to listOf(
            "h264baseline",
            "h264high",
            "h264main",
            "mpeg4",
            "mpeg2video",
            "mpeg1video",
            "vp9",
            "vp8",
            "libxvid",
            
        ),
        "f4v" to listOf("h264baseline", "h264high", "h264main"),
        "m4v" to listOf("flv1", "h264baseline", "h264high", "h264main"),
        "mkv" to listOf(
            "h264baseline",
            "h264high",
            "h264main",
            "mpeg4",
            "mpeg2video",
            "mpeg1video",
            "vp9",
            "vp8",
            
        ),
        "mov" to listOf(
            "h264baseline",
            "h264high",
            "h264main",
            "mpeg4",
            "mpeg2video",
            "mpeg1video",
            "flv1",
            
        ),
        "mpeg" to listOf("mpeg2video", "mpeg1video", ),
        "mpg" to listOf("mpeg2video", "mpeg1video", ),
        "m2ts" to listOf("h264baseline", "h264high", "h264main", "wmv2", ),
        "mts" to listOf(
            "h264baseline",
            "h264high",
            "h264main",
            "mpeg4",
            "mpeg2video",
            "mpeg1video",
            
        ),
        "ts" to listOf(
            "h264baseline",
            "h264high",
            "h264main",
            "mpeg4",
            "mpeg2video",
            "mpeg1video",
            
        ),
        "vob" to listOf("mpeg2video", "mpeg1video", ),
        "ogv" to listOf("libtheora"),
        "webm" to listOf("vp9", "vp8"),
        "wmv" to listOf("wmv2", "wmv1", "h264baseline", "h264high", "h264main")
    )

    fun convertKBtoBit(kb: Long) = kb * 8 * 1024
    fun convertMBtoBit(mb: Long) = mb * 8 * 1024 * 1024

    fun Context.startActivityResult(mediaInput: List<MediaInfo>, mediaOutPut: List<MediaInfo>) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.passMediaOutput(mediaOutPut)
        intent.passMediaInput(mediaInput)
        intent.passActionMedia(MediaAction.ExtractAudio)
        startActivity(intent)
    }

    fun Context.loadImage(path: String, image: ImageView) {
        Glide.with(this)
            .load(path)
            .error(getDrawable(R.drawable.img)!!.setTint(Color.GRAY))
            .into(image);
    }

    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
        .apply { timeZone = TimeZone.getTimeZone("GMT") }

    private val decimalFormat = DecimalFormat("#").apply {
        roundingMode = RoundingMode.CEILING
    }

    fun Float.formatToInt(): Int = decimalFormat.format(this).toInt()
    fun formatTime(time: Long): String = simpleDateFormat.format(time)
    fun convertTimeToMiliSeconds(timeString: String): Long {
        val date = simpleDateFormat.parse(timeString) ?: return 0
        return date.time
    }

    fun MediaInfos.getResolutionMax() =
        this.maxByOrNull { it.resolution!!.width * it.resolution!!.height }

    fun Context.shareMultiple(isVideo: Boolean, videoFilePaths: List<Uri>) {
        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.type = if (isVideo) "video/*" else "image/*"
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(videoFilePaths))
        startActivity(Intent.createChooser(shareIntent, "Share:"))
    }

    fun Context.share(isVideo: Boolean, videoFilePaths: Uri) {
        val videoUris = listOf(videoFilePaths)

        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.type = if (isVideo) "video/*" else "image/*"
        shareIntent.putParcelableArrayListExtra(
            Intent.EXTRA_STREAM,
            ArrayList(videoUris)
        )
        startActivity(Intent.createChooser(shareIntent, "Share:"))
    }

    @SuppressLint("NewApi")
    fun Context.isDarkMode() = resources.configuration.isNightModeActive
    fun getUri(context: Context, path: String): Uri =
        FileProvider.getUriForFile(context, "com.tearas.resizevideo.ui", File(path))

    fun Context.startToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
}